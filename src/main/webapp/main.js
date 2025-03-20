import { signIn, signUp, logout } from "./auth.js";
import { addPassword, loadPasswords } from "./crud.js";

document.getElementById('switchToSignUp').addEventListener('click', () => {
    document.getElementById('signInForm').style.display = 'none';
    document.getElementById('signUpForm').style.display = 'block';
});

document.getElementById('switchToSignIn').addEventListener('click', () => {
    document.getElementById('signUpForm').style.display = 'none';
    document.getElementById('signInForm').style.display = 'block';
});

document.getElementById('signUpBtn').addEventListener('click', async () => {
    const username = document.getElementById('signUpUsername').value;
    const password = document.getElementById('signUpPassword').value;
    await signUp(username, password);
});

document.getElementById('signInBtn').addEventListener('click', async () => {
    const username = document.getElementById('signInUsername').value;
    const password = document.getElementById('signInPassword').value;
    await signIn(username, password);
});

document.getElementById('addPasswordForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const website = document.getElementById('websiteInput').value;
    const password = document.getElementById('passwordInput').value;
    await addPassword(website, password);
});

document.getElementById('logoutBtn').addEventListener('click', logout);

document.addEventListener('DOMContentLoaded', loadPasswords);
