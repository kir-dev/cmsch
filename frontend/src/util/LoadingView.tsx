import { KirDevLogo } from '@/assets/kir-dev-logo.tsx'
import { Loading } from '@/common-components/Loading.tsx'
import { Button } from '@/components/ui/button'
import type { FC, PropsWithChildren } from 'react'
import { l } from './language.ts'
import { Title } from './TitleProvider.tsx'

export type LoadingViewProps = PropsWithChildren & {
  hasError: boolean
  isLoading: boolean
  errorAction?: () => void
  errorTitle?: string
  errorMessage?: string
}

export const LoadingView: FC<LoadingViewProps> = ({ errorAction, hasError, errorTitle, errorMessage, isLoading, children }) => {
  if (hasError) {
    return (
      <div className="flex h-screen flex-col items-center justify-center bg-cover bg-center">
        <Title text={l('error-page-helmet')} />
        <div className="flex flex-col gap-5 rounded-md bg-card p-5 text-card-foreground backdrop-blur-md">
          <h2 className="text-center text-2xl font-bold">{errorTitle}</h2>
          <p className="mt-4 max-w-96 text-center">{errorMessage}</p>
          <div className="mt-4 flex justify-center">
            <Button onClick={errorAction}>Újra</Button>
          </div>
        </div>
      </div>
    )
  }
  if (isLoading) {
    return (
      <div className="flex h-screen flex-col items-center justify-center bg-cover bg-center">
        <div className="flex flex-col rounded-md bg-card p-5 text-card-foreground backdrop-blur-md">
          <Loading />
          <div className="my-3 max-h-40 w-40">
            <KirDevLogo />
          </div>
        </div>
      </div>
    )
  }

  return <>{children}</>
}
