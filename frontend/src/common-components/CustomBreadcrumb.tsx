import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbSeparator } from '@/components/ui/breadcrumb'
import { ChevronRight } from 'lucide-react'
import type { FC } from 'react'
import React from 'react'
import { Link } from 'react-router'

type BreadcrumbProps = {
  items: {
    title?: string
    to?: string
  }[]
  className?: string
}

export const CustomBreadcrumb: FC<BreadcrumbProps> = ({ items, className }) => {
  return (
    <Breadcrumb className={className}>
      <BreadcrumbList>
        {items.map((item, idx) => (
          <React.Fragment key={idx}>
            <BreadcrumbItem>
              <BreadcrumbLink asChild>
                <Link to={item.to ? item.to : '#'} className="text-sm font-medium transition-colors hover:no-underline hover:text-primary">
                  {item.title}
                </Link>
              </BreadcrumbLink>
            </BreadcrumbItem>
            {idx < items.length - 1 && (
              <BreadcrumbSeparator>
                <ChevronRight className="h-4 w-4 text-primary" />
              </BreadcrumbSeparator>
            )}
          </React.Fragment>
        ))}
      </BreadcrumbList>
    </Breadcrumb>
  )
}
