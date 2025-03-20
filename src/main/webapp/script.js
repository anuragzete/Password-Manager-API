// 🚀 Toggle between Sign In and Sign Up forms
document.getElementById('switchToSignUp').addEventListener('click', () => {
    document.getElementById('signInForm').style.display = 'none';
    document.getElementById('signUpForm').style.display = 'block';
});

document.getElementById('switchToSignIn').addEventListener('click', () => {
    document.getElementById('signUpForm').style.display = 'none';
    document.getElementById('signInForm').style.display = 'block';
});

// 🚀 Sign Up
document.getElementById('signUpBtn').addEventListener('click', async () => {
    const username = document.getElementById('signUpUsername').value;
    const password = document.getElementById('signUpPassword').value;

    if (!username || !password) {
        alert('Please fill in all fields!');
        return;
    }

    try {
        const response = await fetch(`/auth?action=signup`, {  // 👈 Using `/auth` endpoint
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();

        if (response.ok && result.status === 'User registered successfully') {
            alert('Sign Up successful! Please Sign In.');
            document.getElementById('switchToSignIn').click();
        } else {
            alert(`Error during Sign Up: ${result.error}`);
        }
    } catch (error) {
        console.error('Sign Up error:', error);
        alert('Failed to sign up. Please try again.');
    }
});

// 🚀 Sign In
document.getElementById('signInBtn').addEventListener('click', async () => {
    const username = document.getElementById('signInUsername').value;
    const password = document.getElementById('signInPassword').value;

    if (!username || !password) {
        alert('Please enter both username and password!');
        return;
    }

    try {
        const response = await fetch(`/Password_Manager_Backend_war_exploded/auth?action=signin`, {  // 👈 Using `/auth` endpoint
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();

        if (response.ok && result.userId) {
            sessionStorage.setItem('username', username);
            sessionStorage.setItem('userId', result.userId);

            document.getElementById('authModal').style.display = 'none';
            document.getElementById('mainContent').style.display = 'block';

            loadPasswords();
        } else {
            alert('Invalid username or password');
        }
    } catch (error) {
        console.error('Sign In error:', error);
        alert('Failed to sign in. Please try again.');
    }
});

// ✅ Add Password
document.getElementById('addPasswordForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const website = document.getElementById('websiteInput').value;
    const password = document.getElementById('passwordInput').value;
    const userId = sessionStorage.getItem('userId');

    if (!userId) {
        alert('User ID is missing! Please sign in again.');
        return;
    }

    if (website && password) {
        try {
            const response = await fetch(`/passwords`, {  // 👈 Using `/passwords` endpoint
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    user_id: userId,
                    site_name: website,
                    username: sessionStorage.getItem('username'),
                    password: password
                })
            });

            if (!response.ok) {
                throw new Error('Failed to save password');
            }

            document.getElementById('websiteInput').value = '';
            document.getElementById('passwordInput').value = '';

            loadPasswords();
        } catch (error) {
            alert('Error saving password. Please try again.');
            console.error('Save error:', error);
        }
    } else {
        alert('Please fill all fields!');
    }
});

// ✅ Load Passwords
async function loadPasswords() {
    const passwordList = document.getElementById('passwordList');
    passwordList.innerHTML = '';

    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        alert('User ID is missing! Please sign in again.');
        return;
    }

    try {
        const response = await fetch(`/passwords?user_id=${userId}`);  // 👈 Using `/passwords` endpoint
        const result = await response.json();

        if (result.passwords.length === 0) {
            passwordList.innerHTML = '<p>No passwords stored yet.</p>';
            return;
        }

        result.passwords.forEach(entry => {
            const listItem = document.createElement('div');
            listItem.className = 'password-item';
            listItem.innerHTML = `
                <div class="password-details">
                    <h3>${entry.site_name}</h3>
                    <p>Username: ${entry.username}</p>
                    <p class="password-field">••••••••</p>
                </div>
                <button class="show-password-btn" data-password="${entry.password}">Show</button>
                <button class="delete-btn" data-site="${entry.site_name}">Delete</button>
            `;
            passwordList.appendChild(listItem);
        });

        // ✅ Show/Hide password functionality
        document.querySelectorAll('.show-password-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const passwordField = e.target.parentElement.querySelector('.password-field');
                const password = e.target.dataset.password;

                if (e.target.textContent === 'Show') {
                    passwordField.textContent = password;
                    e.target.textContent = 'Hide';
                } else {
                    passwordField.textContent = '••••••••';
                    e.target.textContent = 'Show';
                }
            });
        });

        // ✅ Delete password
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const siteName = e.target.dataset.site;

                try {
                    const response = await fetch(`/passwords`, {  // 👈 Using `/passwords` endpoint
                        method: 'DELETE',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            user_id: userId,
                            site_name: siteName
                        })
                    });

                    if (!response.ok) {
                        throw new Error('Failed to delete password');
                    }

                    alert('Password deleted successfully');
                    loadPasswords();
                } catch (error) {
                    alert('Error deleting password. Please try again.');
                    console.error('Delete error:', error);
                }
            });
        });

    } catch (error) {
        passwordList.innerHTML = '<p>Error loading passwords.</p>';
        console.error('Load error:', error);
    }
}

// ✅ Load passwords on page load
document.addEventListener('DOMContentLoaded', loadPasswords);
