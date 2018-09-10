<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
<head>
    <title>Hello voice</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <!--<link rel="stylesheet" type="text/css" href="style.css">-->
</head>
<body>
<h1>русский</h1>
<!--<input type="file" accept="audio/*;capture=microphone">-->
<h2>${message}</h2>
<div id="controls">
    <button id="recordButton">Record33</button>
    <button id="pauseButton" disabled>Pause33</button>
    <button id="stopButton" disabled>Stop</button>
</div>
<div>
    <button style="color: black" id="sendToGoogle">recognize</button>
</div>
<div id="formats">Format: start recording to see sample rate</div>
<h3>Recordings</h3>
<ol id="recordingsList"></ol>
<!--<script src="https://cdn.rawgit.com/mattdiamond/Recorderjs/08e7abd9/dist/recorder.js"></script>
<script src="js/app.js"></script>
-->
</body>
</html>