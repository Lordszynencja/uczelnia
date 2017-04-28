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

const blue1 = [0, 0, 0.25];
const blue2 = [0, 0, 0.5];
const blue3 = [0, 0, 0.75];
const blue4 = [0, 0, 1];

const red1 = [0.333, 0, 0];
const red2 = [0.666, 0, 0];
const red3 = [1, 0, 0];

const yellow1 = [0.333, 0.333, 0];
const yellow2 = [0.666, 0.666, 0];
const yellow3 = [1, 1, 0];

const colors = [black,
blue1, blue2, blue3, blue4,
red1, red2, red3,
yellow1, yellow2, yellow3];
