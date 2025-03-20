
function deriveKey(password, salt) {
    return CryptoJS.PBKDF2(password, CryptoJS.enc.Utf8.parse(salt), {
        keySize: 256 / 32,    // AES-256 key
        iterations: 10000     // Increased security
    });
}

export function encryptPassword(password, salt) {
    const key = deriveKey(password, salt);
    const encrypted = CryptoJS.AES.encrypt(password, key);
    return encrypted.toString();  // Base64-encoded encrypted string
}

export function decryptPassword(encryptedPassword, salt) {
    const key = deriveKey(salt, salt);  // Derive the same key for decryption
    try {
        const decryptedBytes = CryptoJS.AES.decrypt(encryptedPassword, key);
        const decryptedText = decryptedBytes.toString(CryptoJS.enc.Utf8);
        return decryptedText || null;
    } catch (error) {
        console.error("Decryption error:", error);
        return null;
    }
}
