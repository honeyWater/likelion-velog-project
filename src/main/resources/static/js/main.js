document.addEventListener('DOMContentLoaded', function () {
    // 로그아웃 함수 추가
    function logout() {
        fetch('/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // CSRF 토큰이 필요한 경우 여기에 추가
            },
            credentials: 'include' // 쿠키를 포함시키기 위해 추가
        })
            .then(response => {
                if (response.ok) {
                    // 로그아웃 성공 시 홈페이지로 리다이렉트
                    window.location.href = '/';
                } else {
                    console.error('로그아웃 실패');
                }
            })
            .catch(error => {
                console.error('로그아웃 중 오류 발생:', error);
            });
    }

    // HomeTab_selector 요소와 TimeframePicker_aligner 요소 선택
    const selector = document.querySelector('.HomeTab_selector');
    const pickerItems = document.querySelector('.TimeframePicker_aligner');

    if (selector && pickerItems) {

        // 로그아웃 버튼에 이벤트 리스너 추가
        const logoutButton = document.querySelector('.logout-button');
        if (logoutButton) {
            logoutButton.addEventListener('click', function(event) {
                event.preventDefault(); // 기본 동작 방지
                logout(); // 로그아웃 함수 호출
            });
        }

        const picker = document.querySelectorAll('.TimeframePicker_block ul li a');
        const toggleButton = document.querySelector('.toggle-button');
        const menuItems = document.querySelector('.menu-items');

        // 초기 숨김 처리
        pickerItems.style.display = 'none';
        if (menuItems) {
            menuItems.style.display = 'none';
        }

        // HomeTab_selector 클릭 시 메뉴 토글
        selector.addEventListener('click', function (event) {
            event.stopPropagation(); // 이벤트 버블링 방지
            pickerItems.style.display = pickerItems.style.display === 'none' ? 'block' : 'none';
            if (menuItems) {
                menuItems.style.display = 'none'; // toggle-menu 닫기
            }
        });

        // TimeframePicker 항목 클릭 이벤트 리스너
        picker.forEach(link => {
            link.addEventListener('click', function (event) {
                event.preventDefault(); // 기본 링크 동작 방지
                selector.querySelector('.selector-text').textContent = this.textContent.trim(); // 텍스트 업데이트
                window.location.href = this.href; // 링크 이동
                pickerItems.style.display = 'none';
            });
        });

        // toggle-button 클릭 시 메뉴 토글
        if (toggleButton) {
            toggleButton.addEventListener('click', function (event) {
                event.stopPropagation(); // 이벤트 버블링 방지
                menuItems.style.display = menuItems.style.display === 'none' ? 'block' : 'none';
                pickerItems.style.display = 'none'; // TimeframePicker 닫기
            });
        }

        // 다른 곳 클릭 시 모든 메뉴 숨김 처리
        document.addEventListener('click', function () {
            pickerItems.style.display = 'none';
            if (menuItems) {
                menuItems.style.display = 'none';
            }
        });

        // pickerItems 내부 클릭 시 이벤트 전파 방지 (선택 후 바로 닫히는 것 방지)
        pickerItems.addEventListener('click', function (event) {
            event.stopPropagation();
        });

        // menuItems 내부 클릭 시 이벤트 전파 방지 (선택 후 바로 닫히는 것 방지)
        if (menuItems) {
            menuItems.addEventListener('click', function (event) {
                event.stopPropagation();
            });
        }
    } else {
        console.error('HomeTab_selector 또는 TimeframePicker_aligner 요소를 찾을 수 없습니다.');
    }
});
