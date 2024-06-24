document.addEventListener('DOMContentLoaded', function() {
    // 클릭한 항목을 HomeTab_selector의 text로 지정
    const selector = document.querySelector('.HomeTab_selector');
    const picker = document.querySelectorAll('.TimeframePicker_block ul li a');
    const pickerItems = document.querySelector('.TimeframePicker_aligner');

    // 초기에 picker를 숨김 처리
    pickerItems.style.display = 'none';

    picker.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault(); // 기본 링크 동작 방지 (필요한 경우)
            selector.childNodes[0].textContent = this.textContent.trim(); // 텍스트 업데이트
            window.location.href = this.href; // 링크 이동 (필요한 경우)
            pickerItems.style.display = 'none';
        });
    });

    // HomeTab_selector 클릭 시 메뉴 토글
    selector.addEventListener('click', function(event) {
        event.stopPropagation(); // 이벤트 버블링 방지
        pickerItems.style.display = pickerItems.style.display === 'none' ? 'block' : 'none';
    });

    // 다른 곳 클릭 시 메뉴 숨김 처리
    document.addEventListener('click', function() {
        pickerItems.style.display = 'none';
    });

    // picker 내부 클릭 시 이벤트 전파 방지 (선택 후 바로 닫히는 것 방지)
    pickerItems.addEventListener('click', function(event) {
        event.stopPropagation();
    });
});
