<#import "/spring.ftl" as spring />
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Best Shop Ever</title>
    <link rel="stylesheet" href="/bse.css" />
</head>
<body>
<div class="header">
    <div>
        <a href="/bse"><img height="100" src="/img/bse_logo.svg"/></a>
    </div>
    <div class="header-client"></div>
</div>
<div class="client">
    <div class="single-client-area">
        <div>
            <span class="frame-title">Все заказы</span>
        </div>
        <#list orders as order>
            <a href="/bse/order?id=${order.id}" class="order-link">
                <div class="order">
                    <span class="order-title">Заказ #${order.id}<#if order.letycloneUserId??> (letyclone)</#if></span>
                    <#list order.items as item>
                        <div class="item">
                            <img src="${item.product.pictureUrl}" class="product-picture-micro" />
                            <div>
                                ${item.product.name}
                            </div>
                        </div>
                    </#list>
                </div>
            </a>
        </#list>
    </div>
</div>

</body>
</html>