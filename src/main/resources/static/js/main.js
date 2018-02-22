$(document).ready(function () {

    $("#btnSubmit").click(function (event) {
        event.preventDefault();
        fire_ajax_submit();
    });

    $("#files").on("change", function(){
        event.preventDefault();
        fire_ajax_submit();
    });

    $("#files_words").on("change", function(){
        event.preventDefault();
        files_words_submit();
    });


});

function fire_ajax_submit() {

    // Get form
    var form = $('#fileUploadForm')[0];

    var data = new FormData(form);

    data.append("CustomField", "This is some extra data, testing");

    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/api/read",
        //url: "/api/upload/multi",
        data: data,
        //http://api.jquery.com/jQuery.ajax/
        //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {

            $("#result").text(data);
            console.log("SUCCESS : ", data);
            $("#txt_view_byte").html(data.readbyte);
            $("#txt_view_utf8").html(data.strutf8);
            $("#txt_view_ms949").html(data.strms949);
            $("#txt_view_hdfs").html(data.strutf8);

            var option_values;
            option_values += "<option value='utf-8'>"+data.strutf8+"</option>";
            option_values += "<option value='ms949'>"+data.strms949+"</option>";
            $("#selCharset option").remove();
            $("#selCharset").append(option_values);
            if(data.detCharset == 'utf-8') {
                $("#selCharset option:eq(0)").attr('selected','selected')
            } else {
                $("#selCharset option:eq(1)").attr('selected','selected')
            }

            $("#btnSubmit").prop("disabled", false);

        },
        error: function (e) {

            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);

        }
    });

}


function files_words_submit() {

    // Get form
    var form = $('#fileUploadForm')[0];

    var data = new FormData(form);

    data.append("CustomField", "This is some extra data, testing");

    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/api/read_words",
        //url: "/api/upload/multi",
        data: data,
        //http://api.jquery.com/jQuery.ajax/
        //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {

            $("#result").text(data);
            console.log("SUCCESS : ", JSON.stringify(data));

            var col_ids = '';
            for (i=0; i< data.str_id.length; i++) {
                col_ids = '<tr>\n';
                col_ids += '<td col="10">' + data.str_id[i] + '</td>\n';
                col_ids += '<td col="120">' + data.str_orig[i] + '</td>\n';
                col_ids += '<td col="120">' + data.str_parsed[i] + '</td>\n';
                col_ids += '</tr>\n';

                console.log("COL_IDS:"+col_ids);
                $("#txt_view_table").append(col_ids);
            }
            //$("#txt_view_table").html(col_ids);
            //$("#txt_view_id").html(data.str_id);
            //$("#txt_view_orig").html(data.str_orig);
            //$("#txt_view_parsed").html(data.str_parsed);

            $("#btnSubmit").prop("disabled", false);

        },
        error: function (e) {

            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);

        }
    });

}