package com.paypad.parator.utils;

public class ReceiptHtmlContentHelper2 {

    public static String getReceiptHtmlContent(){
        String allText = "";
        String content1 = "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>A simple, clean, and responsive HTML invoice template</title>\n" +
                "    \n" +
                "    <style>\n" +
                "    .invoice-box {\n" +
                "        max-width: 800px;\n" +
                "        margin: auto;\n" +
                "        padding: 30px;\n" +
                "        border: 1px solid #eee;\n" +
                "        box-shadow: 0 0 10px rgba(0, 0, 0, .15);\n" +
                "        font-size: 16px;\n" +
                "        line-height: 24px;\n" +
                "        font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;\n" +
                "        color: #555;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table {\n" +
                "        width: 100%;\n" +
                "        line-height: inherit;\n" +
                "        text-align: left;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table td {\n" +
                "        padding: 5px;\n" +
                "        vertical-align: top;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr td:nth-child(2) {\n" +
                "        text-align: right;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.top table td {\n" +
                "        padding-bottom: 20px;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.top table td.title {\n" +
                "        font-size: 45px;\n" +
                "        line-height: 45px;\n" +
                "        color: #333;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.information table td {\n" +
                "        padding-bottom: 40px;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.heading td {\n" +
                "        background: #eee;\n" +
                "        border-bottom: 1px solid #ddd;\n" +
                "        font-weight: bold;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.details td {\n" +
                "        padding-bottom: 20px;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.item td{\n" +
                "        border-bottom: 1px solid #eee;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.item.last td {\n" +
                "        border-bottom: none;\n" +
                "    }\n" +
                "    \n" +
                "    .invoice-box table tr.total td:nth-child(2) {\n" +
                "        border-top: 2px solid #eee;\n" +
                "        font-weight: bold;\n" +
                "    }\n" +
                "    \n" +
                "    @media only screen and (max-width: 600px) {\n" +
                "        .invoice-box table tr.top table td {\n" +
                "            width: 100%;\n" +
                "            display: block;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        \n" +
                "        .invoice-box table tr.information table td {\n" +
                "            width: 100%;\n" +
                "            display: block;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    /** RTL **/\n" +
                "    .rtl {\n" +
                "        direction: rtl;\n" +
                "        font-family: Tahoma, 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;\n" +
                "    }\n" +
                "    \n" +
                "    .rtl table {\n" +
                "        text-align: right;\n" +
                "    }\n" +
                "    \n" +
                "    .rtl table tr td:nth-child(2) {\n" +
                "        text-align: left;\n" +
                "    }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"invoice-box\">\n" +
                "        <table cellpadding=\"0\" cellspacing=\"0\">\n" +
                "            <tr class=\"top\">\n" +
                "                <td colspan=\"2\">\n" +
                "                    <table>\n" +
                "                        <tr>\n" +
                "                            <td class=\"title\">\n" +
                "                                <img src=\"https://www.sparksuite.com/images/logo.png\" style=\"width:100%; max-width:300px;\">\n" +
                "                            </td>\n" +
                "                            \n" +
                "                            <td>\n" +
                "                                Invoice #: 123<br>\n" +
                "                                Created: January 1, 2015<br>\n" +
                "                                Due: February 1, 2015\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"information\">\n" +
                "                <td colspan=\"2\">\n" +
                "                    <table>\n" +
                "                        <tr>\n" +
                "                            <td>\n" +
                "                                Sparksuite, Inc.<br>\n" +
                "                                12345 Sunny Road<br>\n" +
                "                                Sunnyville, CA 12345\n" +
                "                            </td>\n" +
                "                            \n" +
                "                            <td>\n" +
                "                                Acme Corp.<br>\n" +
                "                                John Doe<br>\n" +
                "                                john@example.com\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"heading\">\n" +
                "                <td>\n" +
                "                    Payment Method\n" +
                "                </td>\n" +
                "                \n" +
                "                <td>\n" +
                "                    Check #\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"details\">\n" +
                "                <td>\n" +
                "                    Check\n" +
                "                </td>\n" +
                "                \n" +
                "                <td>\n" +
                "                    1000\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"heading\">\n" +
                "                <td>\n" +
                "                    Item\n" +
                "                </td>\n" +
                "                \n" +
                "                <td>\n" +
                "                    Price\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"item\">\n" +
                "                <td>\n" +
                "                    Website design\n" +
                "                </td>\n" +
                "                \n" +
                "                <td>\n" +
                "                    $300.00\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"item\">\n" +
                "                <td>\n" +
                "                    Hosting (3 months)\n" +
                "                </td>\n" +
                "                \n" +
                "                <td>\n" +
                "                    $75.00\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"item last\">\n" +
                "                <td>\n" +
                "                    Domain name (1 year)\n" +
                "                </td>\n" +
                "                \n" +
                "                <td>\n" +
                "                    $10.00\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            \n" +
                "            <tr class=\"total\">\n" +
                "                <td></td>\n" +
                "                \n" +
                "                <td>\n" +
                "                   Total: $385.00\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        allText =  content1;

        /*Intent emailIntent = null;
        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(new StringBuilder()
                        .append("<p><b>Some Content</b></p>")
                        .append("<a>http://www.google.com</a>")
                        .append("<small><p>More content</p></small>")
                        .toString())
        );*/

        //String s = Html.fromHtml(allText).toString();


        //SpannedString spannedString = new SpannedString(Html.fromHtml(allText));
        //String htmlEncodedString = Html.toHtml(spannedString);

        return allText;
    }

}
