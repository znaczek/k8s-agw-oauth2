import React, { useEffect, useState } from 'react';
import { getSession } from '../../auth/session-manager';
import { logout } from '../../auth/logout';

const getTimeLeft = () => {
  const sessionExpiresIn = getSession().sessionExpiresIn || '';

  if (sessionExpiresIn === '') {
    return 0;
  }
  const now = Math.round(Date.now() / 1000);
  return parseInt(sessionExpiresIn) - now;
}

export function Session() {

  const [timeLeft, setTimeLeft] = useState(10000);

  useEffect(() => {
    const interval = setInterval(() => {
      const timeLeft = getTimeLeft();
      if (timeLeft < 1) {
        logout()
      } else {
        setTimeLeft(timeLeft);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [])

  return (
    <>
      {timeLeft < 5 && <div className='session'>
        Your session expires in {timeLeft} seconds.
      </div>}
    </>
  );
}
