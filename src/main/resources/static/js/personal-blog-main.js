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

    // 태그 부분
    const tagContainers = document.querySelectorAll('.FlatPostCard_tagsWrapper');
    const domain = /*[[${domain}]]*/ '';

    tagContainers.forEach(container => {
        const tags = container.dataset.tags ? container.dataset.tags.split(',') : [];

        if (tags.length > 0) {
            container.innerHTML = tags.map(tag => `
                <a href="/@${domain}/#" class="tag">${tag}</a>
            `).join('');
        }
        // <a href="/@${domain}/posts?tag=${encodeURIComponent(tag)}" class="tag">${tag}</a>
    });
});