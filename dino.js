
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');
const scoreElem = document.getElementById('score');
const gameOverElem = document.getElementById('gameOver');

// Dino properties
const dino = {
    x: 50,
    y: 220,
    width: 44,
    height: 44,
    vy: 0,
    gravity: 1.2,
    jumpStrength: -18,
    grounded: true
};

// Obstacles
let obstacles = [];
let obstacleTimer = 0;
let score = 0;
let gameOver = false;

function drawDino() {
    ctx.fillStyle = '#222';
    ctx.fillRect(dino.x, dino.y, dino.width, dino.height);
}

function drawGround() {
    ctx.strokeStyle = '#888';
    ctx.beginPath();
    ctx.moveTo(0, 264);
    ctx.lineTo(canvas.width, 264);
    ctx.stroke();
}

function drawObstacles() {
    ctx.fillStyle = '#388e3c';
    for (let obs of obstacles) {
        ctx.fillRect(obs.x, obs.y, obs.width, obs.height);
    }
}

function resetGame() {
    dino.y = 220;
    dino.vy = 0;
    dino.grounded = true;
    obstacles = [];
    obstacleTimer = 0;
    score = 0;
    gameOver = false;
    scoreElem.textContent = 'Score: 0';
    gameOverElem.classList.add('hidden');
}

function update() {
    if (gameOver) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawGround();
    drawDino();
    drawObstacles();

    // Dino physics
    dino.y += dino.vy;
    dino.vy += dino.gravity;
    if (dino.y >= 220) {
        dino.y = 220;
        dino.vy = 0;
        dino.grounded = true;
    }

    // Obstacles logic
    obstacleTimer++;
    if (obstacleTimer > 60 + Math.random() * 60) {
        const height = 30 + Math.random() * 30;
        obstacles.push({ x: canvas.width, y: 264 - height, width: 18, height: height });
        obstacleTimer = 0;
    }
    for (let i = obstacles.length - 1; i >= 0; i--) {
        obstacles[i].x -= 8;
        // Collision detection
        if (
            dino.x < obstacles[i].x + obstacles[i].width &&
            dino.x + dino.width > obstacles[i].x &&
            dino.y < obstacles[i].y + obstacles[i].height &&
            dino.y + dino.height > obstacles[i].y
        ) {
            gameOver = true;
            gameOverElem.classList.remove('hidden');
        }
        // Remove off-screen obstacles
        if (obstacles[i].x + obstacles[i].width < 0) {
            obstacles.splice(i, 1);
            score += 10;
            scoreElem.textContent = 'Score: ' + score;
        }
    }
    if (!gameOver) requestAnimationFrame(update);
}

document.addEventListener('keydown', function(e) {
    if (e.code === 'Space') {
        if (gameOver) {
            resetGame();
            update();
        } else if (dino.grounded) {
            dino.vy = dino.jumpStrength;
            dino.grounded = false;
        }
    }
});

// Start game
resetGame();
update();
