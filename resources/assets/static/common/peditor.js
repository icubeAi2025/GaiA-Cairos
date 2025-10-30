var PEditorTool = function(){
// var parms = [
// 	{
// 		cmd: "aCommandName",
// 		desc: "A DOMString representing the name of the command"
// 	},

// 	{
// 		cmd: "aShowDefaultUI",
// 		desc:
// 			"A Boolean indicating whether the default user interface should be shown. This is not implemented in Mozilla."
// 	},

// 	{
// 		cmd: "aValueArgument",
// 		desc:
// 			"A DOMString representing some commands (such as insertimage) require an extra value argument (the image's url). Pass an argument of null if no argument is needed."
// 	}];
var self = this;

var commands = [

	{
		cmd: "bold", alt: '',
		icon: "bold",
		desc: "Toggles bold on/off for the selection or at the insertion point. (Internet Explorer uses the STRONG tag instead of B.)"
	},
	{
		cmd: "strikeThrough", alt: '',
		icon: "strikethrough",
		desc: "Toggles strikethrough on/off for the selection or at the insertion point."
	},
	{
		cmd: "underline", alt: '',
		icon: "underline",
		desc: "Toggles underline on/off for the selection or at the insertion point."
	},
	{
		cmd: "italic", alt: '',
		icon: "italic",
		desc: "Toggles italics on/off for the selection or at the insertion point. (Internet Explorer uses the EM tag instead of I.)"
	},
	// {
	// 	cmd: "subscript",
	// 	icon: "subscript",
	// 	desc: "Toggles subscript on/off for the selection or at the insertion point."
	// },
	// {
	// 	cmd: "superscript",
	// 	icon: "superscript",
	// 	desc: "Toggles superscript on/off for the selection or at the insertion point."
	// },

	// {
	// 	cmd: "contentReadOnly",
	// 	desc: "Makes the content document either read-only or editable. This requires a boolean true/false to be passed in as a value argument. (Not supported by Internet Explorer.)"
	// },
	// {
	// 	cmd: "copy",
	// 	icon: "clipboard",
	// 	desc: "Copies the current selection to the clipboard. Clipboard capability must be enabled in the user.js preference file. See"
	// },

	// {
	// 	cmd: "cut",
	// 	icon: "scissors",
	// 	desc: "Cuts the current selection and copies it to the clipboard. Clipboard capability must be enabled in the user.js preference file. See"
	// },
	// {
	// 	cmd: "decreaseFontSize",
	// 	desc: "Adds a SMALL tag around the selection or at the insertion point. (Not supported by Internet Explorer.)"
	// },
	// {
	// 	cmd: "delete",
	// 	icon: "scissors",
	// 	desc: "Deletes the current selection."
	// },
	// {
	// 	cmd: "enableInlineTableEditing",
	// 	desc: "Enables or disables the table row and column insertion and deletion controls. (Not supported by Internet Explorer.)"
	// },
	// {
	// 	cmd: "enableObjectResizing",
	// 	desc: "Enables or disables the resize handles on images and other resizable objects. (Not supported by Internet Explorer.)"
	// },
	{
		cmd: "foreColor", alt: '글자색',
		val: "rgba(0,0,0,.5)",
		desc: "Changes a font color for the selection or at the insertion point. This requires a color value string to be passed in as a value argument."
	},
	{
		cmd: "backColor", alt: '배경색',
		val: "red",
		desc: "Changes the document background color. In styleWithCss mode, it affects the background color of the containing block instead. This requires a color value string to be passed in as a value argument. (Internet Explorer uses this to set text background color.)"
	},
	{
		cmd: "fontName", alt: '폰트',
		val: "'Inconsolata', monospace",
		desc: 'Changes the font name for the selection or at the insertion point. This requires a font name string ("Arial" for example) to be passed in as a value argument.'
	},
	{
		cmd: "fontSize", alt: '글자크기',
		val: "1-7",
		icon: "text-height",
		desc: "Changes the font size for the selection or at the insertion point. This requires an HTML font size (1-7) to be passed in as a value argument."
	},


	// {
	// 	cmd: "forwardDelete",
	// 	desc: "Deletes the character ahead of the cursor's position.  It is the same as hitting the delete key."
	// },
	{
		cmd: "heading",
		val: "h3",
		icon: "header",
		desc: 'Adds a heading tag around a selection or insertion point line. Requires the tag-name string to be passed in as a value argument (i.e. "H1", "H6"). (Not supported by Internet Explorer and Safari.)'
	},
	// {
	// 	cmd: "hiliteColor",
	// 	val: "Orange",
	// 	desc: "Changes the background color for the selection or at the insertion point. Requires a color value string to be passed in as a value argument. UseCSS must be turned on for this to function. (Not supported by Internet Explorer.)"
	// },
	// {
	// 	cmd: "increaseFontSize",
	// 	desc: "Adds a BIG tag around the selection or at the insertion point. (Not supported by Internet Explorer.)"
	// },
	{
		cmd: "indent", alt: '들여',
		icon: "indent",
		desc: "Indents the line containing the selection or insertion point. In Firefox, if the selection spans multiple lines at different levels of indentation, only the least indented lines in the selection will be indented."
	},
	{
		cmd: "outdent", alt: '내어',
		icon: "outdent",
		desc: "Outdents the line containing the selection or insertion point."
	},
	// {
	// 	cmd: "insertBrOnReturn",
	// 	desc: "Controls whether the Enter key inserts a br tag or splits the current block element into two. (Not supported by Internet Explorer.)"
	// },

	{
		cmd: "insertOrderedList", alt: '',
		icon: "list-ol",
		desc: "Creates a numbered ordered list for the selection or at the insertion point."
	},
	{
		cmd: "insertUnorderedList", alt: '',
		icon: "list-ul",
		desc: "Creates a bulleted unordered list for the selection or at the insertion point."
	},
	// {
	// 	cmd: "insertParagraph",
	// 	icon: "paragraph",
	// 	desc: "Inserts a paragraph around the selection or the current line. (Internet Explorer inserts a paragraph at the insertion point and deletes the selection.)"
	// },
	// {
	// 	cmd: "insertText",
	// 	val: new Date(),
	// 	icon: "file-text-o",
	// 	desc: "Inserts the given plain text at the insertion point (deletes selection)."
	// },

	// {
	// 	cmd: "justifyFull",
	// 	icon: "align-justify",
	// 	desc: "Justifies the selection or insertion point."
	// },
	{
		cmd: "justifyLeft", alt: '',
		icon: "align-left",
		desc: "Justifies the selection or insertion point to the left."
	},
	{
		cmd: "justifyCenter", alt: '',
		icon: "align-center",
		desc: "Centers the selection or insertion point."
	},
	{
		cmd: "justifyRight", alt: '',
		icon: "align-right",
		desc: "Right-justifies the selection or the insertion point."
	},

	// {
	// 	cmd: "paste",
	// 	icon: "clipboard",
	// 	desc: "Pastes the clipboard contents at the insertion point (replaces current selection). Clipboard capability must be enabled in the user.js preference file. See"
	// },
	// {
	// 	cmd: "redo",
	// 	icon: "repeat",
	// 	desc: "Redoes the previous undo command."
	// },
	{
		cmd: "insertHorizontalRule", alt: '수평줄',
		desc: "Inserts a horizontal rule at the insertion point (deletes selection)."
	},
	{
		cmd: "insertHTML", alt: 'HTML',
		val: "&lt;h3&gt;Life is great!&lt;/h3&gt;",
		icon: "code",
		desc: "Inserts an HTML string at the insertion point (deletes selection). Requires a valid HTML string to be passed in as a value argument. (Not supported by Internet Explorer.)"
	},
	{
		cmd: "insertImage", alt: '이미지',
		val: "http://dummyimage.com/160x90",
		icon: "picture-o",
		desc: "Inserts an image at the insertion point (deletes selection). Requires the image SRC URI string to be passed in as a value argument. The URI must contain at least a single character, which may be a white space. (Internet Explorer will create a link with a null URI value.)"
	},
	{
		cmd: "createLink", alt: '링크',
		val: "https://twitter.com/netsi1964",
		icon: "link",
		desc: "Creates an anchor link from the selection, only if there is a selection. This requires the HREF URI string to be passed in as a value argument. The URI must contain at least a single character, which may be a white space. (Internet Explorer will create a link with a null URI value.)"
	},
	{
		cmd: "unlink", alt: '링크제거',
		icon: "chain-broken",
		desc: "Removes the anchor tag from a selected anchor link."
	},

	{
		cmd: "formatBlock",
		val: "<blockquote>",
		desc: 'Adds an HTML block-style tag around the line containing the current selection, replacing the block element containing the line if one exists (in Firefox, BLOCKQUOTE is the exception - it will wrap any containing block element). Requires a tag-name string to be passed in as a value argument. Virtually all block style tags can be used (eg. "H1", "P", "DL", "BLOCKQUOTE"). (Internet Explorer supports only heading tags H1 - H6, ADDRESS, and PRE, which must also include the tag delimiters &lt; &gt;, such as "&lt;H1&gt;".)'
	},
	{
		cmd: "removeFormat", alt: '포맷제거',
		desc: "Removes all formatting from the current selection."
	},
	// {
	// 	cmd: "selectAll",
	// 	desc: "Selects all of the content of the editable region."
	// },

	{
		cmd: "undo",
		icon: "undo",
		desc: "Undoes the last executed command."
	},

	// {
	// 	cmd: "useCSS ",
	// 	desc: "Toggles the use of HTML tags or CSS for the generated markup. Requires a boolean true/false as a value argument. NOTE: This argument is logically backwards (i.e. use false to use CSS, true to use HTML). (Not supported by Internet Explorer.) This has been deprecated; use the styleWithCSS command instead."
	// },
	{
		cmd: "styleWithCSS",
		desc: "Replaces the useCSS command; argument works as expected, i.e. true modifies/generates style attributes in markup, false generates formatting elements.",
		val: 'true'
	}
];

var commandRelation = {};

function supported(cmd) {
	var css = !!document.queryCommandSupported(cmd.cmd) ? "btn-success" : "btn-error";
	return css;
}

function icon(cmd) {
	return typeof cmd.icon !== "undefined" ? "fa fa-" + cmd.icon : "";
}

function doCommand(cmdKey) {
	var cmd = commandRelation[cmdKey];
	if(! cmd)return alert('Not supported. '+cmdKey);
	if (supported(cmd) === "btn-error") {
		alert("execCommand(“" + cmd.cmd + "”)\nis not supported in your browser");
		return;
	}
	val = typeof cmd.val !== "undefined" ? prompt("Value for " + cmd.cmd + "?", cmd.val) : "";
	document.execCommand(cmd.cmd, false, val || ""); // Thanks to https://codepen.io/bluestreak for finding this bug
}

function init(btnContainer, editorToolVar) {
	var html = '',
		template = '<span><button class="btn btn-xs btn-1" title="%desc%" onmousedown="event.preventDefault();" onclick="%editorToolVar%.doCommand(\'%cmd%\')"><i class="%iconClass%"></i> %btnName%</button>&nbsp;</span>';
	commands.map(function(command, i) {
		var isSupport = !!document.queryCommandSupported(command.cmd)
		if(! isSupport)return;
		commandRelation[command.cmd] = command;
		var temp = template;
		temp = temp.replace(/%iconClass%/gi, icon(command));
		temp = temp.replace(/%desc%/gi, command.desc);
		temp = temp.replace(/%btnClass%/gi, supported(command));
		temp = temp.replace(/%cmd%/gi, command.cmd);
		temp = temp.replace(/%btnName%/gi, command.alt == null ? command.cmd : command.alt);
		html += temp;
	});
	html += '';
	html = html.replace(/%editorToolVar%/gi, editorToolVar);
	btnContainer.innerHTML = html;
}

self.init = init;
self.doCommand = doCommand;

};
// from pen.js

PEditorTool.handlePasteImage = async function(event) {
	// jquery on paste 에서는 이렇게 사용해야 한다. event.originalEvent 로 사용해야 하더라.
	var clipboardData = (event.clipboardData || window.clipboardData);
	if(! (clipboardData && clipboardData.items))return;
	for (var clipboardItem of clipboardData.items) {
		if (clipboardItem.type.indexOf('image') !== -1) {
			var blob = clipboardItem.getAsFile();
			var reader = new FileReader();
			reader.onload = function(event) {
				var dataURL = event.target.result;
				document.execCommand('insertImage', false, dataURL);
			};
			reader.readAsDataURL(blob);
		}
	}
};

// PEditorTool.asyncBlobToBase64 = blobToBase64(blob) {
// 	return new Promise((resolve, _) => {
// 		const reader = new FileReader();
// 		reader.onloadend = () => resolve(reader.result);
// 		reader.readAsDataURL(blob);
// 	});
// };
