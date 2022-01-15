import { Container } from '../@layout/Container'
import { Outlet } from 'react-router-dom'

export function Home() {
  return (
    <Container>
      <Outlet />
      <h1>Főoldal</h1>
    </Container>
  )
}
