$(document).ready(function() {
    // 메뉴 토글 버튼 클릭 시 메뉴 표시/숨김 처리
    $(".toggle-button").click(function() {
        $(".menu-items").toggle();
    });

    // 다른 곳 클릭 시 메뉴 숨김 처리
    $(document).click(function(event) {
        var target = $(event.target);
        if (!target.closest('.toggle-menu').length && !target.closest('.menu-items').length) {
            $(".menu-items").hide();
        }
    });
});