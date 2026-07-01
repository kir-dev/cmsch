import { Loader2 } from 'lucide-react'
import { type ReactNode, useEffect, useState } from 'react'

type LoadingProps = {
  timeout?: number
  children?: ReactNode
}

/**
 * Displays the given loading component or a Spinner after the given timeout or one second.
 * @param timeout Number, Optional - The timeout in millisec before the component appears. Default value is 0
 * @param children ReactNode, Optional - The component to be displayed instead of a brand colored spinner.
 * @constructor
 */
export const Loading = ({ timeout = 0, children }: LoadingProps) => {
  const [show, setShow] = useState<boolean>(false)
  useEffect(() => {
    const timer = setTimeout(() => {
      setShow(true)
    }, timeout)
    return () => clearTimeout(timer)
  }, [timeout])
  if (!show) return null
  return children ? (
    <>{children}</>
  ) : (
    <div className="flex items-center justify-center py-10">
      <Loader2 className="h-12 w-12 animate-spin text-primary" strokeWidth={3} />
    </div>
  )
}
