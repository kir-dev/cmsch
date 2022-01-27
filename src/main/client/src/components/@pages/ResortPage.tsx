import { Page } from '../@layout/Page'
import React from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { RESORTS } from '../../content/resorts'
import { COMMUNITIES } from '../../content/communities'
import { CardListItem } from '../@commons/CardListItem'
import { DataSheet } from '../@commons/DataSheet'
import { CustomBreadcrumb } from '../@commons/CustomBreadcrumb'

type ResortPageProps = {}

export const ResortPage: React.FC<ResortPageProps> = () => {
  const params = useParams()
  const resort = RESORTS.find((r) => r.id === params.name)
  if (!resort) return <Navigate to="/reszortok" />
  const breadcrumbItems = [
    {
      title: 'Reszortok',
      to: '/reszortok'
    },
    {
      title: resort.name,
      color: resort.color
    }
  ]
  return (
    <Page>
      <CustomBreadcrumb items={breadcrumbItems} />
      <DataSheet organization={resort} />
      {COMMUNITIES.filter((c) => c.resortId === resort.id).map((community) => {
        return <CardListItem data={community} link={'/korok/' + community.id} />
      })}
    </Page>
  )
}
