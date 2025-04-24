// components/ParameterHistory/ParameterHistory.tsx

import { ParameterChangeResponse } from '@/features/animal/api/types'
import styles from './ParameterHistory.module.scss'
export const ParameterHistory = ({
	changes,
}: {
	changes: ParameterChangeResponse[]
}) => {
	return (
		<div className={styles.historyContainer}>
			<h3>История изменений параметров</h3>
			{changes.map((change, index) => (
				<div key={index} className={styles.historyItem}>
					<div>{change.parameterName}</div>
					<div>
						{change.oldValue} → {change.newValue}
					</div>
					<div>{new Date(change.changedAt).toLocaleDateString()}</div>
				</div>
			))}
		</div>
	)
}
