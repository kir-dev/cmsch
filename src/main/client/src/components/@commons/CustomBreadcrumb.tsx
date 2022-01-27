import { ChevronRightIcon } from '@chakra-ui/icons'
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink } from '@chakra-ui/react'
import { Link } from 'react-router-dom'

type BreadcrumbProps = {
  items: {
    title?: string
    to?: string
    color?: string
  }[]
}

export const CustomBreadcrumb: React.FC<BreadcrumbProps> = ({ items }) => {
  return (
    <Breadcrumb spacing={2} separator={<ChevronRightIcon color={'brand.500'} />}>
      {items.map((item) => (
        <BreadcrumbItem>
          <BreadcrumbLink
            as={Link}
            to={item.to ? item.to : '#'}
            fontSize="sm"
            fontWeight={500}
            _hover={{
              textDecoration: 'none',
              color: item.color || 'brand.500'
            }}
          >
            {item.title}
          </BreadcrumbLink>
        </BreadcrumbItem>
      ))}
    </Breadcrumb>
  )
}
