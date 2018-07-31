class AnalyzeResultManager {
	constructor() {
		this.result = [];
	}

	update(json, target_el){
		// TODO  以下に相当する処理はレスポンスのエラーコードを見て行う？？

//		if(json_str[0] != "["){
//// 「textarea」はおそらく「target_el」の誤記
//// ただし、正しく修正してしまうと、通信エラーのメッセージが通話履歴に表示されてしまうので
//// 何もせずに終了する（エラー以外のレスポンスが文字列で返信されることはない）
////			target_el.value = textarea.value + json_str + "\n";
//			return;
//		}

		// 更新前の高さ

		var prevHeight = target_el.scrollHeight;

		// 更新
		var _result = this.result;
		var resultTextList = [];
		json.forEach(function (each) {
			var type = each[0];
			if (type === "SOS") {
				// SOSが来たらpushする
				_result.push("");
			}
			if (type === "TMP_RESULT") {
				// TMP_RESULTはセットする
				_result[_result.length - 1] = each[1];
			}
			if (type === "RESULT") {
				// RESULTはセットする
				_result[_result.length - 1] = each[1];
				resultTextList.push(each[1]);
			}
			if (type === "REJECT") {
				// REJECT が来たら消す
				_result.pop();
			}
			// TODO TOO_LONGとNO_DATAもある
		});

		target_el.value = this.result.join('\n\n');


		// 必要に応じて、スクロールバーを末尾に移動

		if ( target_el.scrollHeight <= $( target_el ).innerHeight() ) return resultTextList.join('\n\n');; // スクロールなし

		if ( target_el.scrollTop >= ( prevHeight - $( target_el ).innerHeight() - 5 ) ) {

			// 末尾を表示しているときだけ、末尾に移動する
			// （ユーザがスクロールして上の方を見ているときは移動しない）
			// （末尾条件に5px分の余裕を追加）

			target_el.scrollTop = target_el.scrollHeight;
		}

		return resultTextList.join('\n\n');
	}

	_get_string(json){
		return json.map((i) => i[1] + (i[1].length > 0 ? "\n" : "")).join("");
	}
}

