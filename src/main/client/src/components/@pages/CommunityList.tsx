import { Container } from '../@layout/Container'
import { Outlet } from 'react-router-dom'

export function CommunityList() {
  return (
    <Container>
      <Outlet />
      <h1>Körök</h1>
    </Container>
  )
}
