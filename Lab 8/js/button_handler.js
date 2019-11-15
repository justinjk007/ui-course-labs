function confirm() {
    alert("Event created!");
    window.location.reload();
}

function edit(fieldId) {

    $('html, body').animate({scrollTop: $('#'+fieldId).offset().top}, 200, function() {
        $('#'+fieldId).focus();
    });


    return false;
}

function getInviteList(inviteType) {
    let inviteList = [];
    $('select#'+inviteType+'List  option:selected').each(function() {
        inviteList.push($(this).text());
    });
    return inviteList;
}

function sub() {
    $('#review_eventInvites_selected').hide();
    $('#review_distance').parent().hide();

    $("#frmDetails input[type=text]").each(function() {

        $reviewEle = $('#review_' + this.id);
        if (this && $reviewEle) {
            $reviewEle.text(this.value);
        }
    });

    let inviteType = $('.tab-pane.active')[0].id;
    let invites = getInviteList(inviteType);
    let inviteDistance = $('#distance').val();

    if ($('#individualInvites').hasClass('active')) {
        $('#review_invite_type').text('Individual(s)');
    } else if ($('#groupInvites').hasClass('active')) {
        $('#review_invite_type').text('Group(s)');
    } else if ($('#locInvites').hasClass('active')) {
        $('#review_invite_type').text('Local');
        if (inviteDistance) {
            $('#review_distance').parent().show();
        }
    }

    if (invites && invites.length) {

        $('#review_eventInvites').text(invites.join(", "));

        $('#review_eventInvites_selected').show();
    }


    console.log(invites);
    return false;
}
