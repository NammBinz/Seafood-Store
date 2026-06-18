// Navbar scroll effect
window.addEventListener('scroll', () => {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;
    if (window.scrollY > 80) {
        navbar.style.background = 'rgba(10, 43, 36, 0.98)';
        navbar.style.padding = '0.6rem 0';
    } else {
        navbar.style.background = 'rgba(10, 43, 36, 0.95)';
        navbar.style.padding = '1rem 0';
    }
});

// Đặt món – hiện toast thông báo
function datMon(btn) {
    const ten = btn.getAttribute('data-ten') || 'Món ăn';
    const toastEl = document.getElementById('orderToast');
    const msgEl = document.getElementById('toastMessage');
    if (toastEl && msgEl) {
        msgEl.textContent = `Đã thêm "${ten}" vào đơn hàng!`;
        const toast = new bootstrap.Toast(toastEl, { delay: 2500 });
        toast.show();
    }
}

// Smooth scroll cho anchor links
document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', e => {
        const id = a.getAttribute('href');
        const target = document.querySelector(id);
        if (target) {
            e.preventDefault();
            target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    });
});

// Fade-in animation on scroll
const observerOptions = {
    threshold: 0.12,
    rootMargin: '0px 0px -50px 0px'
};
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
            observer.unobserve(entry.target);
        }
    });
}, observerOptions);

document.querySelectorAll('.mon-card, .menu-card, .why-card').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(30px)';
    el.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    observer.observe(el);
});
