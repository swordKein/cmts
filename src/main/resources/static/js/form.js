$(document).ready(function () {

});

function viewEditPreset(idx) {
    //$("#priority"+idx).hide();
    //$("#priorityInput"+idx).show();
    $('.presetLabel'+idx).hide();
    $('.presetInput'+idx).show();
}

function updateIdx() {
    var idxNumTmp = 1;
    $(".idxNum").each(function () {
        $(this).text(idxNumTmp);
        idxNumTmp++;
    });
}

function addPresetSubForm() {
    var addHtml = "<pre class=\"h4\" size=\"40\"\n" +
        "\t\t\t\t onclick=\"addPresetSubForm();\" >+ Preset # <span id=\"ps_id\" class=\"h4 idxNum\"></span><button type=\"button\"\n" +
        "\t\t\t\t\tclass=\"glyphicon glyphicon-plus\" style=\"margin-left:40px; height:30px; width:30px\" aria-hidden=\"true\"></button></pre>\n" +
        "\t\t\t<p class=\"h5\">\n" +
        "\t\t\t\t<div class=\"form-group\">\n" +
        "\t\t\t\t\t<label for=\"ps_add_url\">URL 추가 :&nbsp;&nbsp;</label>\n" +
        "\t\t\t\t\t<input type=\"text\" class=\"form-control\" id=\"ps_add_url\" name=\"ps_add_url\" placeholder=\"수집 URL 추가 파라미터 입력\" size=\"70\"/>\n" +
        "\t\t\t\t</div>\n" +
        "\t\t\t\t<div class=\"form-group\">\n" +
        "\t\t\t\t\t<label for=\"ps_tag\">수집 TAG :&nbsp;&nbsp;</label>\n" +
        "\t\t\t\t\t<input type=\"text\" class=\"form-control\" id=\"ps_tag\" name=\"ps_id\" placeholder=\"수집 TAG 입력\" size=\"70\"/>\n" +
        "\t\t\t\t</div>\n" +
        "\t\t\t\t<div class=\"form-group\">\n" +
        "\t\t\t\t\t<label for=\"dest_field\">저장 필드 :&nbsp;&nbsp;</label>\n" +
        "\t\t\t\t\t<input type=\"text\" class=\"form-control\" id=\"dest_field\" name=\"dest_field\" placeholder=\"저장될 필드명 입력\" size=\"70\"/>\n" +
        "\t\t\t\t</div>\n" +
        "\t\t\t</p>";


    $("#divPresetList").append(addHtml);

    updateIdx();
}