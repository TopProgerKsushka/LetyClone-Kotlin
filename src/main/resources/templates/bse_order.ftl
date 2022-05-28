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
    <div>
        <a href="/bse/orders">Все заказы</a>
    </div>
</div>
<div class="client">
    <div class="single-client-area">
        <div>
            <span class="frame-title">Заказ #${order.id}</span>
        </div>
        <#if order.letycloneUserId??>
            <span class="frame-subtitle">
                Этот заказ оформлен с использованием кэшбэк-сервиса letyclone
            </span>
        </#if>
        <#list order.items as item>
            <div class="product">
                <img src="${item.product.pictureUrl}" class="product-picture" />
                <div class="product-client">
                    <p><span class="product-name">${item.product.name}</span></p>
                    <p><b>Цена:</b> ₽ ${item.product.price}</p>
                    <#if order.letycloneUserId?? && item.product.cashbackPercent??>
                        <p><b>Кэшбэк:</b> ${item.product.cashbackPercent}%</p>
                        <p><b>Кэшбэк в рублях:</b> ₽ ${item.product.price * (item.product.cashbackPercent / 100)}</p>
                    </#if>
                </div>
                <div>
                    <#if item.canBeRefunded && !item.refunded>
                        <#global thereAreRefundableItems=true />
                        <form method="POST" action="/bse/refund">
                            <#if order.letycloneUserId??>
                                <input type="hidden" name="lc_user_id" value="${order.letycloneUserId}" />
                            </#if>
                            <input type="hidden" name="item_id" value="${item.id}" />
                            <input type="submit" value="Вернуть товар" />
                        </form>
                    </#if>
                </div>
            </div>
        </#list>
        <div class="bottom-bar">
            <#if thereAreRefundableItems??>
                <form method="POST" action="/bse/nonrefundable">
                    <input type="hidden" name="order_id" value="${order.id}" />
                    <input type="submit" value="Все товары больше не возвратные" />
                </form>
            </#if>
        </div>
    </div>
</div>

</body>
</html>