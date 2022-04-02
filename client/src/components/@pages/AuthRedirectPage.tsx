import { FC, useEffect } from 'react'
import { Navigate } from 'react-router-dom'

type Props = {}

export const AuthRedirectPage: FC<Props> = ({}) => {
  // const { onLoginSuccess } = useAuthContext()
  useEffect(() => {
    // todo: call the onLoginSuccess with the response (query params from url)
  }, [])

  return <Navigate to="/profil" />
}
