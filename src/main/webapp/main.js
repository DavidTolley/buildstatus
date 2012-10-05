Event.observe(window, 'load', initMain);

function initMain() {
	initToggle();
}

function initToggle() {
    $$('.showDetailedErrors').each(function(element) {
        var toggler = element.next('.toggle');
        if ( toggler ) {
            toggler.hide();
            element.observe('click', function( event ) {
                var el = Event.element(event);
                var togglebox = el.next('.toggle');
                togglebox.toggle();
                if ( togglebox.visible() ) {
                    el.innerHTML = "Hide Details"
                } else {
                    el.innerHTML = "Show Details"
                }
            });
        }
    });
}