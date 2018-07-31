class Mic {
  constructor(buffersize) {
    this.source = null;
	this.is_recoding = false;
	this.buffersize = buffersize;
	this.recorder = null;
  }

  start(func){
  	if(this.is_recoding) return;


  	navigator.getMedia = navigator.getUserMedia ||
  			navigator.webkitGetUserMedia ||
  			navigator.mozGetUserMedia ||
  			navigator.msGetUserMedia;

  	navigator.getMedia ({ audio:true }, (stream) => {
  		var context = new AudioContext();
  		var source = context.createMediaStreamSource(stream);
  		this.source = source;

  		this.recorder = new Recorder(source);
  		this.recorder && this.recorder.record();

  		var audioBufferArray = [];
  		var scriptProcessor = context.createScriptProcessor(this.buffersize, 1, 1);
  		source.connect(scriptProcessor);
  		// source.connect(context.destination);
  		scriptProcessor.connect(context.destination);
  		scriptProcessor.onaudioprocess = (event) => {
  				var channel = event.inputBuffer.getChannelData(0);
  				var buffer = new Float32Array(this.buffersize);
  				for (var i = 0; i < this.buffersize; i++) {
  						buffer[i] = channel[i];
  				}
  				audioBufferArray.push(buffer)
  				if(audioBufferArray.length >= 5){
  						var newSampleRate = 16000;
  						var data = convert_wav(audioBufferArray, context, newSampleRate);
  						// this.UTF8toBinary(this.header).then((utf) => {
  						//		this.ws.send(this.Combine(utf, data))
  						// })
//  						var blob = new Blob([this._createHeader(),data]);
  						func(data, data.byteLength / (newSampleRate * 2));
  						audioBufferArray = [];
  				}
  		}
  		this.is_recoding = true;
  	}, function(err){ //エラー処理
  		func( false, err );
  	})
  }

  stop(){
  	if(!this.is_recoding) return;

  	this.recorder && this.recorder.stop();

  	this.source.context.close();
		this.is_recoding = false;
  }

}

function convert_wav(audioData, audioContext, newSampleRate) {
	var samples = mergeBuffers(audioData);
	samples = resample(samples, audioContext.sampleRate, newSampleRate);
	var buffer = encodeWAV(samples, newSampleRate);

	// return new Uint8Array(buffer)
	return buffer;
}

function mergeBuffers(audioData) {
	var sampleLength = 0;
	for (var i = 0; i < audioData.length; i++) {
		sampleLength += audioData[i].length;
	}
	var samples = new Float32Array(sampleLength);
	var sampleIdx = 0;
	for (var i = 0; i < audioData.length; i++) {
		for (var j = 0; j < audioData[i].length; j++) {
			samples[sampleIdx] = audioData[i][j];
			sampleIdx++;
		}
	}
	return samples;
}

function resample(samples, oldRate, newRate) {
	const newSamples = new Float32Array(Math.floor(samples.length * (newRate/oldRate)));

	for (let i=0; i<newSamples.length; i++) {
			const startPos = (oldRate/newRate) * i;
			const endPos = (oldRate/newRate) * (i + 1);
			const startIdx = Math.floor(startPos);
			const endIdx = Math.floor(endPos);
			const startMod = (startPos == startIdx) ? 1 : (1 - startPos % 1);
			const endMod = (endPos == endIdx) ? 0 : (endPos % 1);

			let sum = (samples[startIdx] || 0) * startMod + (samples[endIdx] || 0) * endMod;
			for (let i=startIdx+1; i<endIdx; i++) {
					sum += samples[i] || 0;
			}
			newSamples[i] = sum / (endPos - startPos);
	}
	return newSamples;
}

function encodeWAV(samples, sampleRate) {
	// var buffer = new ArrayBuffer(44 + samples.length * 2);
	// var view = new DataView(buffer);

	// this.writeString(view, 0, 'RIFF');	// RIFFヘッダ
	// view.setUint32(4, 32 + samples.length * 2, true); // これ以降のファイルサイズ
	// this.writeString(view, 8, 'WAVE'); // WAVEヘッダ
	// this.writeString(view, 12, 'fmt '); // fmtチャンク
	// view.setUint32(16, 16, true); // fmtチャンクのバイト数
	// view.setUint16(20, 1, true); // フォーマットID
	// view.setUint16(22, 1, true); // チャンネル数
	// view.setUint32(24, sampleRate, true); // サンプリングレート
	// view.setUint32(28, sampleRate * 2, true); // データ速度
	// view.setUint16(32, 2, true); // ブロックサイズ
	// view.setUint16(34, 16, true); // サンプルあたりのビット数
	// this.writeString(view, 36, 'data'); // dataチャンク
	// view.setUint32(40, samples.length * 2, true); // 波形データのバイト数
	// this.floatTo16BitPCM(view, 44, samples); // 波形データ

	var buffer = new ArrayBuffer(samples.length * 2);
	var view = new DataView(buffer);
	floatTo16BitPCM(view,0, samples); // 波形データ

	return buffer;
}

function floatTo16BitPCM(output, offset, input) {
	for (var i = 0; i < input.length; i++, offset += 2){
		var s = Math.max(-1, Math.min(1, input[i]));
		output.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
	}
}

