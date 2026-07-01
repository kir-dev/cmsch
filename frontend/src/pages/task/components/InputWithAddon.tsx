import type { PropsWithChildren } from 'react'

interface InputWithAddonProps extends PropsWithChildren {
  suffix: string | undefined
}

export const InputWithAddon = ({ suffix, children }: InputWithAddonProps) => {
  if (suffix) {
    return (
      <div className="flex w-full items-center">
        <div className="relative flex-1 [&>input]:rounded-r-none [&>textarea]:rounded-r-none">{children}</div>
        <div
          className={
            'inline-flex h-10 items-center justify-center rounded-r-md border ' +
            'border-l-0 border-input bg-muted px-3 py-2 text-sm text-muted-foreground'
          }
        >
          {suffix}
        </div>
      </div>
    )
  }
  return <>{children}</>
}
