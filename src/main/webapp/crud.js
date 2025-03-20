import { encryptPassword, decryptPassword } from './crypto.js';

const loadPasswords = async () => {
    const passwordList = document.getElementById('passwordList');
    passwordList.innerHTML = '';

    const userId = sessionStorage.getItem('userId');
    const encryptedMasterPassword = sessionStorage.getItem('masterPassword');

    if (!userId) {
        alert('User ID is missing! Please sign in again.');
        return;
    }

    try {
        const response = await fetch(`/passwords?user_id=${userId}`);
        const result = await response.json();

        // Cache passwords locally
        sessionStorage.setItem(`passwords_${userId}`, JSON.stringify(result.passwords));

        renderPasswords(result.passwords);
    } catch (error) {
        console.error('Load error:', error);
        passwordList.innerHTML = '<p>Error loading passwords.</p>';
    }
};

const addPassword = async (website, password) => {
    const userId = sessionStorage.getItem('userId');
    const masterPassword = sessionStorage.getItem('masterPassword');

    if (!website || !password) {
        alert('Please fill in all fields!');
        return;
    }

    const encryptedPassword = encryptPassword(password, masterPassword);

    try {
        const response = await fetch(`/passwords`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                user_id: userId,
                site_name: website,
                password: encryptedPassword
            })
        });

        if (!response.ok) {
            throw new Error('Failed to save password');
        }

        // Update cache immediately
        const cachedData = sessionStorage.getItem(`passwords_${userId}`);
        const cachedPasswords = cachedData ? JSON.parse(cachedData) : [];

        cachedPasswords.push({ site_name: website, password: encryptedPassword });
        sessionStorage.setItem(`passwords_${userId}`, JSON.stringify(cachedPasswords));

        renderPasswords(cachedPasswords);
        alert('Password saved successfully!');
    } catch (error) {
        console.error('Add error:', error);
        alert('Error saving password. Please try again.');
    }
};

const deletePassword = async (siteName) => {
    const userId = sessionStorage.getItem('userId');

    try {
        const response = await fetch(`/passwords`, {
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

        // Update cache after deletion
        const cachedData = sessionStorage.getItem(`passwords_${userId}`);
        const cachedPasswords = cachedData ? JSON.parse(cachedData).filter(pw => pw.site_name !== siteName) : [];

        sessionStorage.setItem(`passwords_${userId}`, JSON.stringify(cachedPasswords));
        renderPasswords(cachedPasswords);

        alert('Password deleted successfully!');
    } catch (error) {
        console.error('Delete error:', error);
        alert('Error deleting password. Please try again.');
    }
};

const renderPasswords = (passwords) => {
    const passwordList = document.getElementById('passwordList');
    passwordList.innerHTML = '';

    passwords.forEach(entry => {
        const listItem = document.createElement('div');
        listItem.className = 'password-item';

        listItem.innerHTML = `
            <div class="password-details">
                <h3>${entry.site_name}</h3>
                <p class="password-field">••••••••</p>
            </div>
            <button class="show-password-btn" data-password="${entry.password}">Show</button>
            <button class="delete-btn" data-site="${entry.site_name}">Delete</button>
        `;

        passwordList.appendChild(listItem);
    });

    // Use event delegation for efficiency
    passwordList.addEventListener('click', async (e) => {
        const encryptedMasterPassword = sessionStorage.getItem('masterPassword');

        if (e.target.classList.contains('show-password-btn')) {
            const passwordField = e.target.parentElement.querySelector('.password-field');
            const encryptedPassword = e.target.dataset.password;

            if (e.target.textContent === 'Show') {
                const decryptedPassword = decryptPassword(encryptedPassword, encryptedMasterPassword);
                passwordField.textContent = decryptedPassword;
                e.target.textContent = 'Hide';
            } else {
                passwordField.textContent = '••••••••';
                e.target.textContent = 'Show';
            }
        }

        if (e.target.classList.contains('delete-btn')) {
            const siteName = e.target.dataset.site;
            await deletePassword(siteName);
        }
    });
};

export { loadPasswords, addPassword, deletePassword };
