var Login = function () {


    var handleLogin = function () {
        //登录表单的验证
        $('.login-form').validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                username: {
                    required: true
                },
                password: {
                    required: true
                },
                remember: {
                    required: false
                }
            },

            messages: {
                username: {
                    required: "请输入账号！"
                },
                password: {
                    required: "请输入密码！"
                }
            },

            invalidHandler: function (event, validator) { //display error alert on form submit
                $('.alert-error', $('.login-form')).show();
            },

            highlight: function (element) { // hightlight error inputs
                $(element)
                    .closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function (error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler: function () {
                var postData = {
                    "account": $("#loginAccount").val(),
                    "password": $("#loginPasswd").val()
                };
                var flag=false;
                $.ajax({
                    type: "POST",
                    url: "/rest/login/checkLogin.json",
                    async: false,
                    cache: false,
                    data: postData,
                    dataType: "json",
                    success: function (data) {
                        flag=data;
                    }
                });

                if(flag){
                    window.location.href="/admin/home.html";
                }else{
                    alert("登录失败,请核对信息!");
                }
            }
        });

        //验证表单之后提交
        $('.login-form input').keypress(function (e) {
            if (e.which == 13) {
                if ($('.login-form').validate().form()) {
                    $('.login-form').submit();
                }
                return false;
            }
        });
    }

    var handleForgetPassword = function () {
        $('.forget-form').validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: "",
            rules: {
                email: {
                    required: true,
                    email: true
                }
            },

            messages: {
                email: {
                    required: "Email is required."
                }
            },

            invalidHandler: function (event, validator) { //display error alert on form submit

            },

            highlight: function (element) { // hightlight error inputs
                $(element)
                    .closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function (error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler: function (form) {
                form.submit();
            }
        });

        $('.forget-form input').keypress(function (e) {
            if (e.which == 13) {
                if ($('.forget-form').validate().form()) {
                    $('.forget-form').submit();
                }
                return false;
            }
        });

        jQuery('#forget-password').click(function () {
            jQuery('.login-form').hide();
            jQuery('.forget-form').show();
        });

        jQuery('#back-btn').click(function () {
            jQuery('.login-form').show();
            jQuery('.forget-form').hide();
        });

    }


    var handleRegister = function () {
        $('.register-form').validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: "",
            rules: {
                account: {
                    required: true
                },
                password: {
                    required: true
                },
                rpassword: {
                    equalTo: "#password"
                },
                name: {
                    required: true
                },
                department: {
                    required: true
                },
                area: {
                    required: true
                },
                field: {
                    required: true
                },

                specialPhone: {
                    required: true
                },
                tnc: {
                    required: true
                }
            },

            messages: { // custom messages for radio buttons and checkboxes
                tnc: {
                    required: "请同意相关条款."
                },
                account: {
                    required: "必填."
                },
                password: {
                    required: "必填."
                },
                rpassword: {
                    equalTo: "两次密码不一致."
                },
                name: {
                    required: "必填."
                },
                department: {
                    required: "必填."
                },
                area: {
                    required: "必填."
                },
                field: {
                    required: "必填."
                },
                specialPhone: {
                    required: "必填."
                }
            },

            invalidHandler: function (event, validator) { //display error alert on form submit

            },

            highlight: function (element) { // hightlight error inputs
                $(element)
                    .closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function (error, element) {
                if (element.attr("name") == "tnc") { // insert checkbox errors after the container
                    error.insertAfter($('#register_tnc_error'));
                } else if (element.closest('.input-icon').size() === 1) {
                    error.insertAfter(element.closest('.input-icon'));
                } else {
                    error.insertAfter(element);
                }
            },
            submitHandler: function () {
                var postData = {
                    "account": $("#account").val(),
                    "password": $("#password").val(),
                    "name": $("#name").val(),
                    "department": $("#department").val(),
                    "areaId": $("#area").val(),
                    "field": $("#field").val().toString(),
                    "specialPhone": $("#specialPhone").val(),
                    "telPhone": $("#telPhone").val(),
                    "age": $("#age").val(),
                    "sex": $("#sex").val(),
                    "moPhone": $("#mobilephone").val(),
                    "education": $("#educationalBackground").val(),
                    "other": $("#extraInformation").val()
                };
                var flag=false;
                $.ajax({
                    type: "POST",
                    url: "/rest/login/registerUser.json",
                    async: false,
                    cache: false,
                    data: postData,
                    dataType: "json",
                    success: function (data) {
                        flag=data;
                    }
                });

                if(flag){
                    alert("注册成功!");
                    window.location.href="/";
                }else{
                    alert("注册失败,请核对信息!");
                }
            }
        });

        $('.register-form input').keypress(function (e) {
            if (e.which == 13) {
                if ($('.register-form').validate().form()) {
                    $('.register-form').submit();
                }
                return false;
            }
        });


        jQuery('#register-btn').click(function () {
            jQuery('.login-form').hide();
            jQuery('.register-form').show();
        });

        jQuery('#register-back-btn').click(function () {
            jQuery('.login-form').show();
            jQuery('.register-form').hide();
        });
    }

    return {
        //main function to initiate the module
        init: function () {

            handleLogin();
            handleForgetPassword();
            handleRegister();

            $('#field').select2({
                placeholder: "选择一个方向",
                allowClear: true
            });

            $('#area').select2({
                placeholder: "选择一个地区",
                allowClear: true
            });

        }

    };
}();