import { getSession } from './session-manager';

export const logout = () => {
  const logoutForm = document.createElement('form');
  logoutForm.setAttribute('action', '/logout?session=' + getSession().session)
  logoutForm.setAttribute('method', 'POST')
  document.body.appendChild(logoutForm);
  logoutForm.submit();
}
