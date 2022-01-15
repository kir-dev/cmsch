import { Container } from '../@layout/Container'
import { Outlet } from 'react-router-dom'

export function ResortList() {
  return (
    <Container>
      <Outlet />
      <h1>Reszortok</h1>
    </Container>
  )
}
