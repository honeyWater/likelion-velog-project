$(document).ready(function () {
    // name 중복 확인
    // name을 입력할 수 있는 input text 영역을 벗어나면 동작한다.
    $("#name").on("focusout", function () {
        let name = $("#name").val();

        if (name == '' || name.length == 0) {
            $(".js-name-label").css("color", "red").text("이름을 입력해주세요.");
            return false;
        }

        $.ajax({
            url: 'http://localhost:8080/api/users/check-name',
            data: {
                'name': name
            },
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                if (result == true) {
                    $(".js-name-label").css("color", "black").text("");
                } else {
                    $(".js-name-label").css("color", "red").text("사용 불가능한 이름입니다.");
                }
            }
        }); // End Ajax
    });

    // email 중복 확인
    // email을 입력할 수 있는 input text 영역을 벗어나면 동작한다.
    $("#email").on("focusout", function () {

        let email = $("#email").val();

        if (email == '' || email.length == 0) {
            $(".js-email-label").css("color", "red").text("Email을 입력해주세요.");
            return false;
        }

        //Ajax로 전송
        $.ajax({
            url: 'http://localhost:8080/api/users/check-email',
            data: {
                'email': email
            },
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                if (result == true) {
                    $(".js-email-label").css("color", "black").text("");
                } else {
                    $(".js-email-label").css("color", "red").text("사용 불가능한 Email 입니다.");
                }
            }
        }); //End Ajax
    });

    // password 일치 확인
    // password check를 입력할 수 있는 input text 영역을 벗어나면 일치 여부를 확인한다.
    $("#confirm").on("keyup", function () {
        var password = $("#password").val();
        var confirmPassword = $("#confirm").val();

        if (password == confirmPassword) {
            $(".js-password-label").text("").css("color", "black");
        } else {
            $(".js-password-label").text("비밀번호가 일치하지 않습니다.").css("color", "red");
        }
    });

    // domain 중복 확인
    // domain을 입력할 수 있는 input text 영역을 벗어나면 동작한다.
    $("#domain").on("focusout", function () {

        let domain = $("#domain").val();

        if (domain == '' || domain.length == 0) {
            $(".js-domain-label").css("color", "red").text("도메인을 입력해주세요.");
            return false;
        }

        //Ajax로 전송
        $.ajax({
            url: 'http://localhost:8080/api/users/check-domain',
            data: {
                'domain': domain
            },
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                if (result == true) {
                    $(".js-domain-label").css("color", "black").text("");
                } else {
                    $(".js-domain-label").css("color", "red").text("사용 불가능한 도메인 입니다.");
                }
            }
        }); //End Ajax
    });
})