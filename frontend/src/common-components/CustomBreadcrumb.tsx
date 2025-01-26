import { BreadcrumbLinkProps, ChevronRightIcon } from '@chakra-ui/icons'
import { FC } from 'react'
import { Link } from 'react-router-dom'
import { useColorModeValue } from '../components/ui/color-mode.tsx'
import { BreadcrumbLink, BreadcrumbRoot } from '../components/ui/breadcrumb.tsx'

type BreadcrumbProps = {
  items: {
    title?: string
    to?: string
  }[]
} & BreadcrumbLinkProps

export const CustomBreadcrumb: FC<BreadcrumbProps> = ({ items, ...spaceProps }) => (
  <BreadcrumbRoot {...spaceProps} gap={2} separator={<ChevronRightIcon color={useColorModeValue('brand.500', 'brand.400')} />}>
    {items.map((item, idx) => (
      <BreadcrumbLink
        key={idx}
        as={Link}
        href={item.to ? item.to : '#'}
        fontSize="sm"
        fontWeight={500}
        _hover={{
          textDecoration: 'none',
          color: useColorModeValue('brand.500', 'brand.400')
        }}
      >
        {item.title}
      </BreadcrumbLink>
    ))}
  </BreadcrumbRoot>
)
