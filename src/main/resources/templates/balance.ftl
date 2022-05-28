<#import "/spring.ftl" as spring />

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вывод средств – Letyclone</title>
    <link rel="stylesheet" href="/index.css" />
    <link rel="stylesheet" href="/balance.css" />
</head>
<body>
<div class="header">
    <div class="header-content">
        <a href="/">
            <img src="/img/lc_logo.svg" alt="LetyClone" class="logo"/>
        </a>
        <div class="buttons-container">
            <#if username??>
                <div class="username-block">
                    <span class="username">${username}</span>
                    <div>
                        <a class="poor-link logout-link" href="/user_api/logout">Выйти</a>
                    </div>
                </div>
                <div>
                    <a class="poor-link" href="/balance">${actualBalance} руб.</a> /
                    <span class="uncredited-cashback"><a class="poor-link" href="/balance">${ghostBalance} руб.</a></span>
                </div>
            <#else>
                <div class="button bg-grey" onclick="showLoginForm();">
                    Вход
                </div>
                <div class="button bg-yellow" onclick="showRegisterForm();">
                    Регистрация
                </div>
            </#if>
        </div>
    </div>
</div>
<div class="subheader">
    <div class="subheader-content">
        <b>Добро пожаловать на <i>letyclone</i></b>
    </div>
</div>
<div class="content">
    <div class="page">
        <h1 class="page-title">
            Вывод средств
        </h1>

        <div class="balance-grid">
            <div class="balance-block">
                <div class="balance-frame">
                    <div class="arrow-icon green"></div>
                    <div class="balance-frame-client">
                        <span class="balance-frame-caption">На балансе:</span>
                        <span class="balance">${actualBalance} руб.</span>
                    </div>
                </div>
                <div class="button-block">
                    <form method="POST" action="/user_api/withdraw">
                        <input type="submit" class="big-button bg-yellow" value="Вывести средства" />
                    </form>
                </div>
            </div>
            <div class="balance-block">
                <div class="balance-frame">
                    <div class="arrow-icon blue"></div>
                    <div class="balance-frame-client gray-text">
                        <span class="balance-frame-caption">Ожидает начисления:</span>
                        <span class="balance">${ghostBalance} руб.</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="interlayer">
    <#if !username??>
        <div class="login-register-form" id="login-form">
            <span class="form-caption">Вход</span>
            <form method="post" action="/user_api/login">
                    <input type="text" name="email" class="form-input"/>
                    <span class="float-label">Email</span>
                    <input type="password" name="password" class="form-input"/>
                    <span class="float-label">Пароль</span>
                    <input type="submit" class="form-button" value="Войти"/>
            </form>
        </div>
        <div class="login-register-form" id="register-form">
            <span class="form-caption">Регистрация</span>
            <form method="post" action="/user_api/register">
                <input type="text" name="email" class="form-input"/>
                <span class="float-label">Введи свой email</span>
                <input type="password" name="password" class="form-input"/>
                <span class="float-label">Придумай пароль</span>
                <input type="submit" class="form-button" value="Зарегистрироваться"/>
            </form>
        </div>
    </#if>
    <div id="message-box">
        <div id="message-box-text"></div>
        <div class="button bg-yellow" onclick="hideInterlayer();">Ок</div>
    </div>
</div>

<script type="text/javascript" src="/index.js"></script>
<#if message??>
    <script type="text/javascript">
        showMessageBox("${message}");
    </script>
</#if>
</body>
</html>
