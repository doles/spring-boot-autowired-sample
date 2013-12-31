devcodes = {};

devcodes.profiler = (function ($) {
	config: {
		
	}
	
	var _lightboxBg;
	var _profilerData;
	var _profilerContainer;
	
	function _createProfilerTab() {
		$('<div/>')
			.addClass('profiler-tab')
			.html(_profilerData.rootEntry.duration+'ms')
			.appendTo('body')
			.click(function() {
				_showLightboxBG();
			})
			.show();
	}
	
	function _createLightboxBG() {
		_lightboxBg = $('<div/>')
			.addClass('profiler-overlay')
			.appendTo('body')
			.click(function() {
				onOverlayClick();
			});
	}
	
	function _createProfilerContainer() {
		_profilerContainer = $('<div/>').appendTo('body').addClass('profiler-container');
		
		$.get(config.location+'/profiler.tmpl.htm', function(templates) {
			$('head').append(templates);
			$('#profilerTemplate').tmpl( _profilerData ).appendTo(_profilerContainer);
			$(".show-queries").click(_onShowQueries);
			
			prettyPrint();
		});
	}
	
	function onOverlayClick() {
		_lightboxBg.hide();
		_profilerContainer.hide();
	}
	
	function _showLightboxBG() {
		_lightboxBg.show();
		_profilerContainer.show();
	}
	
	function _processProfilerData() {
		_profilerData = profilerOutput;
		_createProfilerTab();
	}
	
	function _init(options) {
		if(options) {
			config = options;	
		}
		
		if(profilerOutput) {
			_processProfilerData();
			_createLightboxBG();
			_createProfilerContainer();
		}
	}
	
	function _onShowQueries(e) {
		var link = $(this),
                depth = link.parent().attr('class').substr('toggle-'.length);

		$(this).closest('tr').nextUntil('.depth-'+(parseInt(depth)-1)).show();
		
		$(this).click(_onHideQueries);
	}
	
	function _onHideQueries() {
		var link = $(this),
                depth = link.parent().attr('class').substr('toggle-'.length);

		$(this).closest('tr').nextUntil('.depth-'+(parseInt(depth)-1)).hide();
		
		$(this).click(_onShowQueries);
	}
	
	function _renderIndent(depth) {
		return depth*15+'px';
	}
	
	function _formatDate(date) {
		var trueDate = new Date(date);
		
		return trueDate.getDate()+"/"+trueDate.getMonth()+"/"+trueDate.getFullYear()+" at "+trueDate.getHours()+":"+trueDate.getMinutes()+"."+trueDate.getSeconds();
	}
	
	function _formatEntry(entry) {
		if(entry.tag == 'SQL') {
			return '<pre class="prettyprint lang-sql">'+entry.displayString+'</pre>';
		}
		
		return entry.displayString;
	}
	
	function _loadIncludes(options) {
		
	}
	
	return {
		init: _init,
		renderIndent: _renderIndent,
		formatDate: _formatDate,
		formatEntry: _formatEntry,
		loadIncludes: _loadIncludes
	}
}(jQuery));

$('body').ajaxSuccess (
    function (event, requestData)
    {
        //alert("AJAX LOL");
    }
);


/*(function(open) {  
    XMLHttpRequest.prototype.open = function(method, url, async, user, pass) {  
        this.addEventListener("readystatechange", function() {  
            alert("AJAX");
        }, false);  
        open.call(this, method, url, async, user, pass);
    };  
})(XMLHttpRequest.prototype.open);*/