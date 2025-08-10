import { ChevronRightIcon } from '@chakra-ui/icons'
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, SpaceProps, useColorModeValue } from '@chakra-ui/react'
import { FC } from 'react'
import { Link } from 'react-router'

type BreadcrumbProps = {
  items: {
    title?: string
    to?: string
  }[]
} & SpaceProps

export const CustomBreadcrumb: FC<BreadcrumbProps> = ({ items, ...spaceProps }) => {
  const color = useColorModeValue('brand.500', 'brand.400')
  return (
    <Breadcrumb {...spaceProps} spacing={2} separator={<ChevronRightIcon color={useColorModeValue('brand.500', 'brand.400')} />}>
      {items.map((item, idx) => (
        <BreadcrumbItem key={idx}>
          <BreadcrumbLink
            as={Link}
            to={item.to ? item.to : '#'}
            fontSize="sm"
            fontWeight={500}
            _hover={{ textDecoration: 'none', color: color }}
          >
            {item.title}
          </BreadcrumbLink>
        </BreadcrumbItem>
      ))}
    </Breadcrumb>
  )
}
