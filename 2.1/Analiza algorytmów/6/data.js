var v1 = [];
var v2 = [];
var v3 = [];
var c1 = [];
var c2 = [];
var c3 = [];

var canvas = document.getElementById('canv');
canvas.width = 900;
canvas.height = 900;
var gl = canvas.getContext('webgl');

const length = 50;

var vBuffer = gl.createBuffer();
var cBuffer = gl.createBuffer();

const black = [0, 0, 0];

const blue = [0, 0, 1];
const red = [1, 0, 0];
const yellow = [1, 1, 0];
const orange = [1, 0.6, 0.2];
const purple = [1, 0, 0.8];
const green = [0, 1, 0];
const lightblue = [0.7, 0.7, 1];
const grey = [0.7, 0.7, 0.7];
const darkgreen = [0, 0.4, 0];

const colors = [blue, red, yellow, orange, purple, green, lightblue, grey, darkgreen];
const colors_names = ['blue', 'red', 'yellow', 'orange', 'purple', 'green', 'lightblue', 'grey', 'darkgreen'];
