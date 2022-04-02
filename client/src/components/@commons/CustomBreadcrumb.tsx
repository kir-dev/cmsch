import { ChevronRightIcon } from '@chakra-ui/icons'
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, SpaceProps, useColorModeValue } from '@chakra-ui/react'
import { Link } from 'react-router-dom'

type BreadcrumbProps = {
  items: {
    title?: string
    to?: string
  }[]
} & SpaceProps

export const CustomBreadcrumb: React.FC<BreadcrumbProps> = ({ items, ...spaceProps }) => {
  return (
    <Breadcrumb {...spaceProps} spacing={2} separator={<ChevronRightIcon color={useColorModeValue('brand.500', 'brand.600')} />}>
      {items.map((item, idx) => (
        <BreadcrumbItem key={idx}>
          <BreadcrumbLink
            as={Link}
            to={item.to ? item.to : '#'}
            fontSize="sm"
            fontWeight={500}
            _hover={{
              textDecoration: 'none',
              color: useColorModeValue('brand.500', 'brand.600')
            }}
          >
            {item.title}
          </BreadcrumbLink>
        </BreadcrumbItem>
      ))}
    </Breadcrumb>
  )
}
