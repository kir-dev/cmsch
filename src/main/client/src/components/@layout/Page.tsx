import React from 'react'
import { Container } from './Container'
import { Outlet } from 'react-router-dom'

export const Page: React.FC = ({ children, ...props }) => {
  return (
    <Container {...props}>
      <Outlet />
      {children}
    </Container>
  )
}
