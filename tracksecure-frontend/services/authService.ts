import { User } from '../types';

// Simuler une table d'utilisateurs en mémoire
const users: (User & { password?: string })[] = [
  { username: 'admin', password: 'admin123', role: 'administrateur', email: 'admin@tracksecure.com' },
  { username: 'user', password: 'user123', role: 'utilisateur', email: 'user@tracksecure.com' },
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
export const createUser = (username: string, password: string, email: string): Promise<User> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (users.some((u) => u.username === username)) {
        return reject(new Error('Ce nom d\'utilisateur existe déjà.'));
      }
      if (users.some((u) => u.email === email)) {
        return reject(new Error('Cette adresse e-mail est déjà utilisée.'));
      }
      const newUser = { username, password, role: 'utilisateur' as const, email };
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

/**
 * Simule la mise à jour d'un utilisateur existant.
 */
export const updateUser = (originalUsername: string, updatedData: { username: string; email: string }): Promise<User> => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            const userIndex = users.findIndex(u => u.username === originalUsername);
            if (userIndex === -1) {
                return reject(new Error("L'utilisateur n'a pas été trouvé."));
            }

            // Vérifier les conflits de nom d'utilisateur
            if (originalUsername !== updatedData.username && users.some(u => u.username === updatedData.username)) {
                return reject(new Error('Ce nom d\'utilisateur est déjà pris.'));
            }
            // Vérifier les conflits d'e-mail
            if (users[userIndex].email !== updatedData.email && users.some(u => u.email === updatedData.email)) {
                return reject(new Error('Cette adresse e-mail est déjà utilisée.'));
            }

            const currentUser = users[userIndex];
            const updatedUser = {
                ...currentUser,
                username: updatedData.username,
                email: updatedData.email,
            };
            users[userIndex] = updatedUser;

            const { password, ...userWithoutPassword } = updatedUser;
            resolve(userWithoutPassword);
        }, 300);
    });
};

/**
 * Simule la suppression d'un utilisateur.
 */
export const deleteUser = (username: string): Promise<void> => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (username === 'admin') {
                return reject(new Error("L'administrateur principal ne peut pas être supprimé."));
            }
            const userIndex = users.findIndex(u => u.username === username);
            if (userIndex === -1) {
                return reject(new Error("L'utilisateur n'a pas été trouvé."));
            }
            users.splice(userIndex, 1);
            resolve();
        }, 300);
    });
};