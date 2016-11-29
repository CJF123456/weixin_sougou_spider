var utils = {
	b64_423 : function(E) {
		var D = new Array("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
				"u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "-", "_");
		var F = new String();
		for ( var C = 0; C < E.length; C++) {
			for ( var A = 0; A < 64; A++) {
				if (E.charAt(C) == D[A]) {
					var B = A.toString(2);
					F += ("000000" + B).substr(B.length);
					break
				}
			}
			if (A == 64) {
				if (C == 2) {
					return F.substr(0, 8);
				} else {
					return F.substr(0, 16);
				}
			}
		}
		return F;
	},
	b2i : function(D) {
		var A = 0;
		var B = 128;
		for ( var C = 0; C < 8; C++, B = B / 2) {
			if (D.charAt(C) == "1") {
				A += B;
			}
		}
		return String.fromCharCode(A);
	},

	b64_decodex : function(D) {
		var B = new Array();
		var C;
		var A = "";
		for (C = 0; C < D.length; C += 4) {
			A += utils.b64_423(D.substr(C, 4));
		}
		for (C = 0; C < A.length; C += 8) {
			B += utils.b2i(A.substr(C, 8));
		}
		return B;
	},

	utf8to16 : function(I) {
		var D, F, E, G, H, C, B, A, J;
		D = [];
		G = I.length;
		F = E = 0;
		while (F < G) {
			H = I.charCodeAt(F++);
			switch (H >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				D[E++] = I.charAt(F - 1);
				break;
			case 12:
			case 13:
				C = I.charCodeAt(F++);
				D[E++] = String.fromCharCode(((H & 31) << 6) | (C & 63));
				break;
			case 14:
				C = I.charCodeAt(F++);
				B = I.charCodeAt(F++);
				D[E++] = String.fromCharCode(((H & 15) << 12) | ((C & 63) << 6)
						| (B & 63));
				break;
			case 15:
				switch (H & 15) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					C = I.charCodeAt(F++);
					B = I.charCodeAt(F++);
					A = I.charCodeAt(F++);
					J = ((H & 7) << 18) | ((C & 63) << 12) | ((B & 63) << 6)
							| (A & 63) - 65536;
					if (0 <= J && J <= 1048575) {
						D[E] = String.fromCharCode(((J >>> 10) & 1023) | 55296,
								(J & 1023) | 56320);
					} else {
						D[E] = "?";
					}
					break;
				case 8:
				case 9:
				case 10:
				case 11:
					F += 4;
					D[E] = "?";
					break;
				case 12:
				case 13:
					F += 5;
					D[E] = "?";
					break;
				}
			}
			E++;
		}
		return D.join("");
	},

	tmpl : function(str, data) {
		var fn = new Function("obj",
				"var p=[],print=function(){p.push.apply(p,arguments);};"
						+ "with(obj){p.push('"
						+ str.replace(/[\r\t\n]/g, " ").split("<%").join("\t")
								.replace(/((^|%>)[^\t]*)'/g, "$1\r").replace(
										/\t=(.*?)%>/g, "',$1,'").split("\t")
								.join("');").split("%>").join("p.push('")
								.split("\r").join("\\'")
						+ "');}return p.join('');");
		return data ? fn(data) : fn;
	},
	addIframe : function(container, url, callback) {
		var iframe = document.createElement('iframe');
		iframe.style.height = '1px';
		iframe.style.width = '1px';
		iframe.style.visibility = 'hidden';
		iframe.src = url;

		if (iframe.attachEvent) {
			iframe.attachEvent("onload", function() {
				callback && callback();
			});
		} else {
			iframe.onload = function() {
				callback && callback();
			};
		}

		container.appendChild(iframe);

	},
	uuid : function() {
		function s4() {
			return Math.floor((1 + Math.random()) * 0x10000).toString(16)
					.substring(1);
		}
		;
		return s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4();
	}
};
function getToken() {
	return  utils.uuid();
}