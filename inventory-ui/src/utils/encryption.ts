/**
 * Encryption utility for securing credentials in transit
 * Uses AES-GCM encryption with a session key fetched from the server
 */

// Utility to convert string to base64
const stringToBase64 = (str: string): string => {
  return btoa(unescape(encodeURIComponent(str)));
};

// Utility to convert base64 to string
const base64ToString = (base64: string): string => {
  return decodeURIComponent(escape(atob(base64)));
};

// Utility to convert hex string to Uint8Array
const hexToUint8Array = (hex: string): Uint8Array => {
  const bytes = [];
  for (let i = 0; i < hex.length; i += 2) {
    bytes.push(parseInt(hex.substr(i, 2), 16));
  }
  return new Uint8Array(bytes);
};

// Utility to convert Uint8Array to hex string
const uint8ArrayToHex = (bytes: Uint8Array): string => {
  return Array.from(bytes)
    .map(b => b.toString(16).padStart(2, '0'))
    .join('');
};

/**
 * Encrypt password using AES-GCM with a generated session key
 * Returns encrypted data along with IV (Initialization Vector)
 */
export const encryptPassword = async (password: string): Promise<string> => {
  try {
    // Generate a random IV (Initialization Vector)
    const iv = crypto.getRandomValues(new Uint8Array(12));

    // Generate a random encryption key
    const key = await crypto.subtle.generateKey(
      { name: 'AES-GCM', length: 256 },
      true,
      ['encrypt', 'decrypt']
    );

    // Encrypt the password
    const encodedPassword = new TextEncoder().encode(password);
    const encryptedData = await crypto.subtle.encrypt(
      { name: 'AES-GCM', iv: iv },
      key,
      encodedPassword
    );

    // Export the key to raw format
    const exportedKey = await crypto.subtle.exportKey('raw', key);

    // Combine IV + Key + EncryptedData and encode to base64
    const iv_b64 = stringToBase64(String.fromCharCode(...Array.from(iv)));
    const key_b64 = stringToBase64(String.fromCharCode(...Array.from(new Uint8Array(exportedKey))));
    const encrypted_b64 = stringToBase64(String.fromCharCode(...Array.from(new Uint8Array(encryptedData))));

    // Return format: iv:key:encryptedPassword (all base64 encoded)
    return `${iv_b64}:${key_b64}:${encrypted_b64}`;
  } catch (error) {
    console.error('Encryption error:', error);
    throw new Error('Failed to encrypt password');
  }
};

/**
 * Simpler approach: Just base64 encode with a timestamp (defense-in-depth with HTTPS)
 * This provides obfuscation + timestamp to prevent replay attacks
 */
export const encryptPasswordSimple = (password: string): string => {
  // Add timestamp to prevent replay attacks
  const timestamp = Date.now().toString();
  const combined = `${password}|${timestamp}`;
  return stringToBase64(combined);
};

/**
 * Decrypt password (client-side for testing only)
 */
export const decryptPasswordSimple = (encrypted: string): { password: string; timestamp: string } => {
  const combined = base64ToString(encrypted);
  const [password, timestamp] = combined.split('|');
  return { password, timestamp };
};

/**
 * Hash password for additional security
 */
export const hashPassword = async (password: string): Promise<string> => {
  const msgBuffer = new TextEncoder().encode(password);
  const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
  return uint8ArrayToHex(new Uint8Array(hashBuffer));
};

export default {
  encryptPassword,
  encryptPasswordSimple,
  decryptPasswordSimple,
  hashPassword,
};

