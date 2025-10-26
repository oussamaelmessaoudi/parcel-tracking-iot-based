import { User } from '../types';

// Simuler une table d'utilisateurs en mémoire
const users: (User & { password?: string })[] = [
  { username: 'admin', password: 'admin123', role: 'administrateur' },
  { username: 'user', password: 'user123', role: 'utilisateur' },
];

/**
 * Simule une connexion utilisateur en vérifiant la liste en mémoire.
 */
export const login = (username: string, password: string): Promise<User> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = users.find(
        (u) => u.username === username && u.password === password
      );
      if (user) {
        // Ne pas renvoyer le mot de passe
        const { password, ...userWithoutPassword } = user;
        resolve(userWithoutPassword);
      } else {
        reject(new Error("Nom d'utilisateur ou mot de passe incorrect."));
      }
    }, 500);
  });
};

/**
 * Simule la création d'un nouvel utilisateur.
 */
export const createUser = (username: string, password: string): Promise<User> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (users.some((u) => u.username === username)) {
        return reject(new Error('Ce nom d\'utilisateur existe déjà.'));
      }
      const newUser = { username, password, role: 'utilisateur' as const };
      users.push(newUser);
      const { password: pw, ...userWithoutPassword } = newUser;
      resolve(userWithoutPassword);
    }, 300);
  });
};

/**
 * Récupère la liste de tous les utilisateurs (sans mot de passe).
 */
export const getUsers = (): Promise<User[]> => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(users.map(u => {
                const { password, ...userWithoutPassword } = u;
                return userWithoutPassword;
            }));
        }, 200);
    });
};
