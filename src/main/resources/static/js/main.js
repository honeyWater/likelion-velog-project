document.addEventListener('DOMContentLoaded', function() {
    // HomeTab_selector 요소와 TimeframePicker_aligner 요소 선택
    const selector = document.querySelector('.HomeTab_selector');
    const pickerItems = document.querySelector('.TimeframePicker_aligner');

    if (selector && pickerItems) {
        const picker = document.querySelectorAll('.TimeframePicker_block ul li a');
        const toggleButton = document.querySelector('.toggle-button');
        const menuItems = document.querySelector('.menu-items');

        // 초기 숨김 처리
        pickerItems.style.display = 'none';
        if (menuItems) {
            menuItems.style.display = 'none';
        }

        // HomeTab_selector 클릭 시 메뉴 토글
        selector.addEventListener('click', function(event) {
            event.stopPropagation(); // 이벤트 버블링 방지
            pickerItems.style.display = pickerItems.style.display === 'none' ? 'block' : 'none';
            if (menuItems) {
                menuItems.style.display = 'none'; // toggle-menu 닫기
            }
        });

        // TimeframePicker 항목 클릭 이벤트 리스너
        picker.forEach(link => {
            link.addEventListener('click', function(event) {
                event.preventDefault(); // 기본 링크 동작 방지
                selector.querySelector('.selector-text').textContent = this.textContent.trim(); // 텍스트 업데이트
                window.location.href = this.href; // 링크 이동
                pickerItems.style.display = 'none';
            });
        });

        // toggle-button 클릭 시 메뉴 토글
        if (toggleButton) {
            toggleButton.addEventListener('click', function(event) {
                event.stopPropagation(); // 이벤트 버블링 방지
                menuItems.style.display = menuItems.style.display === 'none' ? 'block' : 'none';
                pickerItems.style.display = 'none'; // TimeframePicker 닫기
            });
        }

        // 다른 곳 클릭 시 모든 메뉴 숨김 처리
        document.addEventListener('click', function() {
            pickerItems.style.display = 'none';
            if (menuItems) {
                menuItems.style.display = 'none';
            }
        });

        // pickerItems 내부 클릭 시 이벤트 전파 방지 (선택 후 바로 닫히는 것 방지)
        pickerItems.addEventListener('click', function(event) {
            event.stopPropagation();
        });

        // menuItems 내부 클릭 시 이벤트 전파 방지 (선택 후 바로 닫히는 것 방지)
        if (menuItems) {
            menuItems.addEventListener('click', function(event) {
                event.stopPropagation();
            });
        }
    } else {
        console.error('HomeTab_selector 또는 TimeframePicker_aligner 요소를 찾을 수 없습니다.');
    }
});
