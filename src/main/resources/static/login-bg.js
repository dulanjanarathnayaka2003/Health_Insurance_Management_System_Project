/* Interactive background for login page: lightweight particle network with parallax.
   - Brand palette: deep green (#1a3c34), mid green (#16965c), accent (#38ef7d)
   - Performance-aware: DPR scaling, visibility pause, resize throttle, FPS guard
   - Accessibility: respects prefers-reduced-motion
*/
(function(){
  const canvas = document.getElementById('bg-canvas');
  if (!canvas) return;

  // Respect user preference for reduced motion
  const reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (reduceMotion) return; // CSS also hides the canvas

  const ctx = canvas.getContext('2d');
  let dpr = Math.min(window.devicePixelRatio || 1, 2);
  let width = 0, height = 0;
  let particles = [];
  let animationId = null;
  let lastTime = 0;
  let paused = document.hidden;

  const colors = ['#16965c', '#38ef7d', '#1a3c34'];
  const lineColor = 'rgba(26, 60, 52,'; // alpha appended dynamically

  const mouse = { x: 0, y: 0, active: false };
  window.addEventListener('mousemove', (e) => {
    mouse.x = e.clientX;
    mouse.y = e.clientY;
    mouse.active = true;
  }, { passive: true });
  window.addEventListener('mouseleave', () => { mouse.active = false; });

  function rand(min, max){ return Math.random() * (max - min) + min; }

  function resize(){
    const rect = { w: window.innerWidth, h: window.innerHeight };
    width = rect.w; height = rect.h;
    dpr = Math.min(window.devicePixelRatio || 1, 2);
    canvas.width = Math.floor(width * dpr);
    canvas.height = Math.floor(height * dpr);
    canvas.style.width = width + 'px';
    canvas.style.height = height + 'px';
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    initParticles();
  }

  let resizeTimer = null;
  window.addEventListener('resize', () => {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(resize, 150);
  });

  document.addEventListener('visibilitychange', () => {
    paused = document.hidden;
    if (!paused) {
      lastTime = performance.now();
      loop(lastTime);
    }
  });

  function particleCountForArea(){
    // Roughly 1 particle per 16k px^2, capped for performance
    const area = width * height;
    return Math.max(30, Math.min(140, Math.floor(area / 16000)));
  }

  function initParticles(){
    const count = particleCountForArea();
    particles = new Array(count).fill(0).map(() => ({
      x: rand(0, width),
      y: rand(0, height),
      vx: rand(-0.35, 0.35),
      vy: rand(-0.35, 0.35),
      r: rand(1.2, 2.6),
      color: colors[Math.floor(rand(0, colors.length))]
    }));
  }

  function draw(){
    ctx.clearRect(0, 0, width, height);

    // Connect nearby particles with faint lines
    const linkDist = Math.min(160, Math.max(90, Math.sqrt(width*height) / 8));

    for (let i = 0; i < particles.length; i++) {
      const p = particles[i];

      // Parallax: slight drift toward mouse when active
      if (mouse.active) {
        const dx = (mouse.x - p.x) * 0.0009;
        const dy = (mouse.y - p.y) * 0.0009;
        p.vx += dx; p.vy += dy;
      }

      // Velocity clamp
      const maxV = 0.6;
      if (p.vx > maxV) p.vx = maxV; else if (p.vx < -maxV) p.vx = -maxV;
      if (p.vy > maxV) p.vy = maxV; else if (p.vy < -maxV) p.vy = -maxV;

      // Move & wrap softly at edges
      p.x += p.vx; p.y += p.vy;
      if (p.x < -10) p.x = width + 10; else if (p.x > width + 10) p.x = -10;
      if (p.y < -10) p.y = height + 10; else if (p.y > height + 10) p.y = -10;

      // Draw particle
      ctx.beginPath();
      ctx.fillStyle = p.color;
      ctx.globalAlpha = 0.95;
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      ctx.fill();

      // Links
      for (let j = i + 1; j < particles.length; j++) {
        const q = particles[j];
        const dx = p.x - q.x;
        const dy = p.y - q.y;
        const dist2 = dx*dx + dy*dy;
        const max2 = linkDist * linkDist;
        if (dist2 < max2) {
          // Alpha fades with distance
          const alpha = 1 - (dist2 / max2);
          ctx.beginPath();
          ctx.strokeStyle = lineColor + Math.max(0.05, Math.min(0.28, alpha * 0.35)) + ')';
          ctx.lineWidth = 1;
          ctx.moveTo(p.x, p.y);
          ctx.lineTo(q.x, q.y);
          ctx.stroke();
        }
      }
    }
    ctx.globalAlpha = 1;
  }

  function loop(timestamp){
    if (paused) return;
    const dt = timestamp - lastTime;
    lastTime = timestamp;

    // Simple frame skip if too slow
    if (dt < 1000 / 24) {
      draw();
    }
    animationId = requestAnimationFrame(loop);
  }

  // Kick off
  resize();
  lastTime = performance.now();
  animationId = requestAnimationFrame(loop);

  // Cleanup on unload
  window.addEventListener('pagehide', () => { if (animationId) cancelAnimationFrame(animationId); });
})();
