    function confirmComment(msg) {
        var decision = confirm(msg);
        return decision;
    }

    function addItem_ajaxx() {

        // Get form
        var title = $("#title").val();
        alert('#input title:' + title);
    }

    function addItem_ajax() {

        // Get form
        var title = $("#title").val();
        console.log('#input title:' + title);

        var data = new FormData();
        data.append("title", title);

        $.ajax({
            type: "POST",
            url: "/admin/items/add",
            //url: "/api/upload/multi",
            data: data,
            //http://api.jquery.com/jQuery.ajax/
            //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
            processData: false, //prevent jQuery from automatically transforming the data into a query string
            contentType: false,
            cache: false,
            timeout: 6000,
            success: function (data) {
                $("#result").text(data);
                console.log("SUCCESS : ", data);
                document.location.reload();
            },
            error: function (e) {
                console.log("ERROR : ", e);
                alert("ERROR : " + e);
                document.location.reload();
            }
        });
    }

    function delItem_ajax(idx, title) {

        // Get form
        console.log('#delete by idx:' + idx);
        var result = confirm($("#title").val() + " 영화를 삭제하시겠습니까?");

        if(result) {
            var data = new FormData();
            data.append("idx", idx);

            $.ajax({
                type: "POST",
                url: "/admin/items/del",
                //url: "/api/upload/multi",
                data: data,
                //http://api.jquery.com/jQuery.ajax/
                //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
                processData: false, //prevent jQuery from automatically transforming the data into a query string
                contentType: false,
                cache: false,
                timeout: 6000,
                success: function (data) {
                    $("#result").text(data);
                    console.log("SUCCESS : ", data);
                    document.location.reload();
                },
                error: function (e) {
                    console.log("ERROR : ", e);
                    alert("ERROR : " + e);
                    document.location.reload();
                }
            });
        }
    }

    function schedRetry_ajax(sc_id) {

        // Get form
        console.log('#schedRetry sc_id:' + sc_id);

        var data = new FormData();
        data.append("sc_id", sc_id);

        $.ajax({
            type: "POST",
            url: "/admin/sched/retry",
            //url: "/api/upload/multi",
            data: data,
            //http://api.jquery.com/jQuery.ajax/
            //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
            processData: false, //prevent jQuery from automatically transforming the data into a query string
            contentType: false,
            cache: false,
            timeout: 6000,
            success: function (data) {
                $("#result").text(data);
                console.log("SUCCESS : ", data);
                document.location.reload();
            },
            error: function (e) {
                console.log("ERROR : ", e);
                alert("ERROR : " + e);
                document.location.reload();
            }
        });
    }


    function editTarget(idx) {

        //$("#tgTitle" + idx).hide();
        $(".lables"+ idx).hide();
        $(".iconBefore"+ idx).hide();

        //$("#tgTitleInput" + idx).show();
        $(".inputes" + idx).show();
        $(".iconAfter" + idx).show();
    }

    function cancelEditTarget(idx) {
        $(".lables"+ idx).show();
        $(".iconBefore"+ idx).show();

        $(".inputes" + idx).hide();
        $(".iconAfter" + idx).hide();
    }


    function editPreset(idx) {
        $(".plables"+ idx).hide();
        $(".piconBefore"+ idx).hide();

        $(".pinputes" + idx).show();
        $(".piconAfter" + idx).show();

        //$("#targetListTable").css("position","absolute");
        //$("#targetListTable").css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");

        //$("#targetListTable").scrollLeft(800);
        //$("#targetListTable").animate({scrollLeft: $("#presetListTable").scrolLeft()}, 0);
        $("#psPs_tagInput"+idx).focus();
    }

    function cancelEditPreset(idx) {
        $(".plables"+ idx).show();
        $(".piconBefore"+ idx).show();

        $(".pinputes" + idx).hide();
        $(".piconAfter" + idx).hide();
    }


    function editSaveTarget(idx) {
        $.ajax({
            type:"POST",
            url : "/admin/properties/target/edit",
            async: false,
            data : {
                "tg_id": idx,
                "title":$("#tgTitleInput" + idx).val(),
                "descript":$("#tgDescriptInput" + idx).val(),
                "tg_url":$("#tgTg_urlInput" + idx).val(),
                "tg_url_param1":$("#tgTg_url_param1Input" + idx).val(),
                //"tg_url_param1":$("#tgTg_url_param2Input" + idx).val(),
                //"tg_url_param1":$("#tgTg_url_param3Input" + idx).val(),
                "param1":$("#tgParam1Input" + idx).val(),
                "regid":$("#tgRegidInput" + idx).val(),
                "stat":$("#tgStatInput" + idx).val(),
                "fail_count1":$("#tgFail_count1Input" + idx).val(),
                "fail_count2":$("#tgFail_count2Input" + idx).val()
            },
            success: function(result, status) {

                console.log("#Result:"+result);
                console.log("#Result rtcode:"+result.rtcode);

                if(result != "") {

                    $("#tgTitle" + idx).text($("#tgTitleInput" + idx).val());
                    $("#tgDescript" + idx).text($("#tgDescriptInput" + idx).val());
                    $("#tgTg_url" + idx).text($("#tgTg_urlInput" + idx).val());
                    $("#tgTg_url_param1" + idx).text($("#tgTg_url_param1Input" + idx).val());
                    $("#tgParam1" + idx).text($("#tgParam1Input" + idx).val());
                    $("#tgRegid" + idx).text($("#tgRegidInput" + idx).val());
                    $("#tgStat" + idx).text($("#tgStatInput" + idx).val());
                    $("#tgFail_count1" + idx).text($("#tgFail_count1Input" + idx).val());
                    $("#tgFail_count2" + idx).text($("#tgFail_count2Input" + idx).val());

                    cancelEditTarget(idx);

                    alert("수정되었습니다");
                }
            },
            error:function(request, status, error) {
                alert("삭제 실패. 시스템 오류가 발생했습니다");
            }
        });

    }


    function removeTarget(idx) {
        var decision = confirmComment("정말 삭제하시겠습니까?\n");

        if(decision) {
            $.ajax({
                type:"POST",
                url : "/admin/properties/target/remove",
                async: false,
                data : {
                    "tg_id": idx
                },
                success: function(result, status) {
                    if(result != "") {
                        alert("삭제되었습니다");
                        window.location = "/admin/properties/list";
                    }


                },
                error:function(request, status, error) {
                    alert("삭제 실패. 시스템 오류가 발생했습니다.");
                }
            });
        }

    }


    function editSavePreset(idx) {
        $.ajax({
            type:"POST",
            url : "/admin/properties/preset/edit",
            async: false,
            data : {
                "ps_id": idx,
                "ps_tag":$("#psPs_tagInput" + idx).val(),
                "ps_type":$("#psPs_typeInput" + idx).val(),
                "dest_field":$("#psDest_fieldInput" + idx).val(),
                "ps_add_url":$("#psPs_add_urlInput" + idx).val(),
                "descriptp":$("#psDescriptpInput"+ idx).val(),
                "dest_charset":$("#psDest_charsetInput"+ idx).val()
            },
            success: function(result, status) {

                console.log("#Result:"+result);
                console.log("#Result rtcode:"+result.rtcode);

                if(result != "") {

                    $("#psPs_tag" + idx).text($("#psPs_tagInput" + idx).val());
                    $("#psPs_type" + idx).text($("#psPs_typeInput" + idx).val());
                    $("#psDest_field" + idx).text($("#psDest_fieldInput" + idx).val());
                    $("#psPs_add_url" + idx).text($("#psPs_add_urlInput" + idx).val());
                    $("#psDescriptp" + idx).text($("#psDescriptpInput" + idx).val());
                    $("#psDest_charset" + idx).text($("#psDest_charsetInput" + idx).val());

                    cancelEditPreset(idx);

                    alert("수정되었습니다");
                }
            },
            error:function(request, status, error) {
                alert("삭제 실패. 시스템 오류가 발생했습니다");
            }
        });

    }


    function removePreset(idx) {
        var decision = confirmComment("정말 삭제하시겠습니까?\n");

        if(decision) {
            $.ajax({
                type:"POST",
                url : "/admin/properties/preset/remove",
                async: false,
                data : {
                    "ps_id": idx
                },
                success: function(result, status) {
                    if(result != "") {
                        alert("삭제되었습니다");
                        window.location = "/admin/properties/list";
                    }


                },
                error:function(request, status, error) {
                    alert("삭제 실패. 시스템 오류가 발생했습니다.");
                }
            });
        }

    }

    function search_cine21(pageno) {
        var searchTxt = $("#searchTxt").val();
        //alert("#search:"+searchTxt);
        location.href="/admin/movie_cine21/list?page="+pageno+"&search="+searchTxt;
    }

    function onKeyDown(pageno)
    {
        if(event.keyCode == 13)
        {
            search_cine21(pageno);
        }
    }

    function search_items(pageno) {
        var searchTxt = $("#searchTxt").val();
        //alert("#search:"+searchTxt);
        location.href="/admin/items/list?page="+pageno+"&search="+searchTxt;
    }

    function onKeyDownItems(pageno)
    {
        if(event.keyCode == 13)
        {
            search_items(pageno);
        }
    }