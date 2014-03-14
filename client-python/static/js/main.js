/*
Copyright 2014 Google Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

function toggleUi(clazz, link) {
  $(".toggleable").hide();
  $("." + clazz).show();

  $('#mainNav li').removeClass('active');
  $(link).parent().addClass('active');
}

function showError(message) {
  showAlert(message, 'danger');
}

function showMessage(message) {
  showAlert(message, 'info');
}

function showAlert(message, type) {
  var alert = $('<div class="alert alert-info alert-dismissable"/>')
      .addClass('alert-' + type)
      .text(message).appendTo($("body"));
  closeButton().attr('data-dismiss', 'alert').appendTo(alert);
  alert.css('margin-left', -1 * alert.width()/2);

  setTimeout(function() {
    alert.alert('close')
  }, 2000);
}

function closeButton() {
  return $('<button type="button" class="close" aria-hidden="true">&times;</button>');
}

function getBackend() {
  return $('#backend').val();
}

function getBackendName() {
  return $('#backend option:selected').text();
}

function saveSettings() {
  $.post('/settings', {backend: getBackend()}).done(function() {
    searchReadsets();
  });
  return false;
}

function removeReadset(name, id) {
  readgraph.removeReadset(id);
}

function addReadset(name, id) {
  if (readgraph.hasReadset(id)) {
    showMessage(name + ' has already been selected');
    return;
  }
  showMessage('Loading ' + name);
  $('#readsetSearch').modal('hide');

  var readsetList = $('#activeReadsets').empty();
  var li = $('<li>', {'id': 'readset-' + id, 'class': 'list-group-item'})
      .text(name).appendTo(readsetList);
  closeButton().appendTo(li).click(function() {
    li.remove();
    removeReadset(name, id);
  });

  $.getJSON('/api/readsets', {'readsetId': id})
      .done(function(res) {
        readgraph.addReadset(id, res.fileData[0].refSequences);
      }).fail(function() {
        li.remove();
      });
}

function searchReadsets(button) {
  if (button) {
    button = $(button);
    button.button('loading');
  }
  var div = $('#readsetResults').html('<img src="static/img/spinner.gif"/>');
  $.getJSON('/api/readsets')
      .done(function(res) {
        div.empty();
        $.each(res.readsets, function(i, data) {
          var name = getBackendName() + ": " + data.name;
          $('<a/>', {'href': '#', 'class': 'list-group-item'}).text(name).appendTo(div).click(function() {
            addReadset(name, data.id);
          });
        })
      }).always(function() {
        button && button.button('reset');
      });
  return false;
}

// Show the about popup when the page loads
// And prep the initial readset search
$(document).ready(function() {
  $("#about").modal('show');

  $(document).ajaxError(function(e, xhr) {
    showError("Sorry, the api request failed for some reason. " +
        "(" + xhr.responseText + ")");
  });

  // TODO: Save current readsets in a cookie
  searchReadsets();
});