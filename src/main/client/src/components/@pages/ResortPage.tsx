import { Page } from '../@layout/Page'
import React from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { Resorts } from '../../content/resorts'
import { Communities } from '../../content/communities'
import { CardListItem } from '../@commons/CardListItem'
import { DataSheet } from '../@commons/DataSheet'

type ResortPageProps = {}

export const ResortPage: React.FC<ResortPageProps> = () => {
  const params = useParams()
  const resort = Resorts.find((r) => r.id === params.name)
  if (!resort) return <Navigate to="/reszortok" />
  return (
    <Page>
      <DataSheet organization={resort} />
      {Communities.filter((c) => c.resortId === resort.id).map((community) => {
        return <CardListItem data={community} link={'/korok/' + community.id} />
      })}
    </Page>
  )
}
