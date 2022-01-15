import { Container } from '../@layout/Container'
import { Outlet } from 'react-router-dom'

export function ResortPage() {
  return (
    <Container>
      <Outlet />
      <h1>Kör</h1>
    </Container>
  )
}
