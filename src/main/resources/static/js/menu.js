document.addEventListener('DOMContentLoaded', function () {
    // 로그아웃 함수
    function logout() {
        fetch('/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // CSRF 토큰이 필요한 경우 여기에 추가
            },
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/';
                } else {
                    console.error('로그아웃 실패');
                }
            })
            .catch(error => {
                console.error('로그아웃 중 오류 발생:', error);
            });
    }

    const toggleButton = document.querySelector('.toggle-button');
    const menuItems = document.querySelector('.menu-items');

    if (toggleButton && menuItems) {
        // 초기 숨김 처리
        menuItems.style.display = 'none';

        // 토글 버튼 클릭 시 메뉴 토글
        toggleButton.addEventListener('click', function (event) {
            event.stopPropagation();
            menuItems.style.display = menuItems.style.display === 'none' ? 'block' : 'none';
        });

        // 다른 곳 클릭 시 메뉴 숨김 처리
        document.addEventListener('click', function () {
            menuItems.style.display = 'none';
        });

        // 메뉴 내부 클릭 시 이벤트 전파 방지
        menuItems.addEventListener('click', function (event) {
            event.stopPropagation();
        });

        // 로그아웃 버튼에 이벤트 리스너 추가
        const logoutButton = menuItems.querySelector('a[href="#"][onclick="logout(\'/logout\')"]');
        if (logoutButton) {
            logoutButton.addEventListener('click', function(event) {
                event.preventDefault();
                event.stopPropagation();
                logout();
            });
        }
    }
});