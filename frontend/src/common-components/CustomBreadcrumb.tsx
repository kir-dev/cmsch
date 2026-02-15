import { ChevronRightIcon } from '@chakra-ui/icons'
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, type SpaceProps } from '@chakra-ui/react'
import type { FC } from 'react'
import { Link } from 'react-router'
import { useBrandColor } from '../util/core-functions.util.ts'

type BreadcrumbProps = {
  items: {
    title?: string
    to?: string
  }[]
} & SpaceProps

export const CustomBreadcrumb: FC<BreadcrumbProps> = ({ items, ...spaceProps }) => {
  const color = useBrandColor(500, 400)
  return (
    <Breadcrumb {...spaceProps} spacing={2} separator={<ChevronRightIcon color={color} />}>
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
