import { encryptPassword } from './crypto.js';
import { loadPasswords } from "./crud.js";

const fetchRequest = async (url, method, data = {}) => {
    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(`Error: ${response.status} - ${errorMsg}`);
        }

        try {
            return await response.json();
        } catch (jsonError) {
            throw new Error('Failed to parse JSON response');
        }
    } catch (error) {
        console.error(`Fetch error: ${error.message}`);
        alert(`Request failed: ${error.message}`);
        throw error;
    }
};

const signUp = async (username, password) => {
    if (!username || !password) {
        alert('Please fill in all fields!');
        return;
    }

    const encryptedMasterPassword = encryptPassword(password, username);

    try {
        const result = await fetchRequest('/auth?action=signup', 'POST', {
            username,
            password: encryptedMasterPassword
        });

        if (result.status === 'User registered successfully') {
            alert('Sign Up successful! Please Sign In.');
            document.getElementById('switchToSignIn').click();
        } else {
            alert(`Error during Sign Up: ${result.error}`);
        }
    } catch (error) {
        console.error('Sign Up error:', error);
    }
};

const signIn = async (username, password) => {
    if (!username || !password) {
        alert('Please enter both username and password!');
        return;
    }

    const encryptedMasterPassword = encryptPassword(password, username);

    try {
        const result = await fetchRequest('/auth?action=signin', 'POST', {
            username,
            password: encryptedMasterPassword
        });

        if (result.userId) {
            sessionStorage.setItem('username', username);
            sessionStorage.setItem('userId', result.userId);
            sessionStorage.setItem('masterPassword', encryptedMasterPassword);

            document.getElementById('authModal').style.display = 'none';
            document.getElementById('mainContent').style.display = 'block';

            await loadPasswords();
        } else {
            alert('Invalid username or password');
        }
    } catch (error) {
        console.error('Sign In error:', error);
    }
};

const logout = () => {
    sessionStorage.clear();
    window.location.reload();
};

export { signUp, signIn, logout };
