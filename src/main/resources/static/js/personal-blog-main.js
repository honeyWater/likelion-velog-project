document.addEventListener('DOMContentLoaded', function() {
    const tabs = document.querySelectorAll('.VelogTab_wrapper a');
    const indicator = document.querySelector('.VelogTab_indicator');

    function positionIndicator(activeTab) {
        const tabPosition = activeTab.offsetLeft;
        indicator.style.transform = `translateX(${tabPosition}px)`;
    }

    // 초기 위치 설정
    const activeTab = document.querySelector('.VelogTab_active');
    if (activeTab) {
        positionIndicator(activeTab);
    }

    // 탭 클릭 이벤트
    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            tabs.forEach(t => t.classList.remove('VelogTab_active'));
            this.classList.add('VelogTab_active');
            positionIndicator(this);
        });
    });
});